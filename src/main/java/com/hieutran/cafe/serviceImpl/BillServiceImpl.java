package com.hieutran.cafe.serviceImpl;

import com.hieutran.cafe.DAO.BillDAO;
import com.hieutran.cafe.JWT.JwtFilter;
import com.hieutran.cafe.constants.CafeConstants;
import com.hieutran.cafe.model.Bill;
import com.hieutran.cafe.service.IBillService;
import com.hieutran.cafe.utils.CafeUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;


@Slf4j
@Service
public class BillServiceImpl implements IBillService {

    @Autowired
    BillDAO billDAO;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("Inside generateReport(), requestMap: {}", requestMap);
        try {
            String filename;
            if (validateRequestMap(requestMap)){
                if (requestMap.containsKey("isGenerate") && !(Boolean)requestMap.get("isGenerate")){
                    filename = (String) requestMap.get("uuid");
                }
                else {
                    filename = CafeUtils.getUUID();
                    requestMap.put("uuid", filename);
                    insertBill(requestMap);
                }

                String data = "Name " + requestMap.get("name") +
                            "\nContact Number: " + requestMap.get("contactNumber") +
                            "\nEmail: " + requestMap.get("email") +
                            "\nPayment: " + requestMap.get("paymentMethod");

                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(CafeConstants.STORE_LOCATION+"\\" + filename + ".pdf"));

               /* Set rectangle for pdf file */
                document.open();
                setRectanglePdf(document);

                /* Set header for pdf file */
                Paragraph chunk = new Paragraph("Cafe Management System", getFont("Header"));
                chunk.setAlignment(Element.ALIGN_CENTER);
                document.add((chunk));

                Paragraph paragraph = new Paragraph(data + "\n\n", getFont("Data"));
                document.add(paragraph);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                addTableHeader(table);

                JSONArray jsonArray = CafeUtils.getJsonArrayFromString((String) requestMap.get("productDetail"));
                log.info("jsonArray: {}", jsonArray);
                for (int i=0; i<jsonArray.length(); i++){
                    addRow(table, CafeUtils.getMapFromJson(jsonArray.getString(i)));
                }
                document.add(table);

                Paragraph footer = new Paragraph("Total: " + requestMap.get("total") + "\n" +
                        "Thank you for visiting. Please visit again!!", getFont("Data"));
                document.add(footer);
                document.close();
                return new ResponseEntity<>("{\"uuid\": \"" + filename + "\"}", HttpStatus.OK);
            }
            else return CafeUtils.getResponseEntity("Required data not found", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {
        List<Bill> list = new ArrayList<>();
        if (jwtFilter.isAdmin()){
            list = billDAO.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }
        else {
            list = billDAO.findByCreatebyOrderByIdDesc(jwtFilter.getCurrentUser());
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> getPDf(Map<String, Object> requestMap) {
        log.info("Inside getPdf, param: {}", requestMap);
        try {
            byte[] byteArray = new byte[0];
            if ( !requestMap.containsKey("uuid") && validateRequestMap(requestMap))
                return new ResponseEntity<>(byteArray, HttpStatus.BAD_REQUEST);
            String filePath = CafeConstants.STORE_LOCATION + "\\" + (String) requestMap.get("uuid") + ".pdf";
            if (CafeUtils.isFileExist(filePath)){
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            }
            else {
                requestMap.put("isGenerate", false);
                generateReport(requestMap);
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        try {
            Optional<Bill> optional = billDAO.findById(id);
            if (optional.isPresent()){
                billDAO.deleteById(id);
                return CafeUtils.getResponseEntity("Delete Successfully.", HttpStatus.OK);
            }
            else return CafeUtils.getResponseEntity("Bill is not exist", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /*==================================================================================================
                               PRIVATE FUNCTIONS
    ==================================================================================================*/
    private boolean validateRequestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod") &&
                requestMap.containsKey("productDetail") &&
                requestMap.containsKey("total");
    }

    private void insertBill(Map<String, Object> requestMap) {
        try {
            Bill bill = new Bill();
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setName((String) requestMap.get("name"));
            bill.setEmail((String) requestMap.get("email"));
            bill.setContactNumber((String) requestMap.get("contactNumber"));
            bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
            bill.setTotal(Integer.parseInt((String) requestMap.get("total")));
            bill.setProductDetail((String) requestMap.get("productDetail"));
            bill.setCreateby(jwtFilter.getCurrentUser());
            billDAO.save(bill);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRectanglePdf(Document document) throws DocumentException {
        log.info("Inside setRectanglePdf, {}", document);
        Rectangle rectangle = new Rectangle(577, 825, 18, 15);
        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
        rectangle.setBorderColor(BaseColor.BLACK);
        rectangle.setBorderWidth(1);
        document.add(rectangle);
    }

    private Font getFont(String type) {
        log.info("Inside getFont");
        switch (type){
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
            default:
                return new Font();
        }
    }

    private void addTableHeader(PdfPTable table) {
        log.info("Inside addTableHeader");
        Stream.of("Name", "Category", "Quantity", "Price", "Sub Total")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    header.setBackgroundColor(BaseColor.YELLOW);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });
    }

    private void addRow(PdfPTable table, Map<String, Object> data) {
        log.info("Inside addRow");
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString( (Double) data.get("price") ));
        table.addCell(Double.toString( (Double) data.get("total") ));


    }

    private byte[] getByteArray(String filePath) throws Exception{
        File file = new File(filePath);
        InputStream targetStream = new FileInputStream(file);
        byte[] byteArray = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;
    }
}
