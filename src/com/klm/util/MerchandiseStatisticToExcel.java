package com.klm.util;

import com.klm.cons.impl.Floor;
import com.klm.cons.impl.House;
import com.klm.cons.impl.Room;
import com.klm.persist.Merchandise;
import com.klm.util.impl.MerchandiseInfo;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 2/15/12
 * Time: 6:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class MerchandiseStatisticToExcel {

    private static final String[] TITLE = {
            "房间", "商品", "单位", "单价", "数量", "合计"
    };
    private static final String[] CELL_INDEX = {
            "A", "B", "C", "D", "E", "F"
    };

    public synchronized static void toExcel(final House house, final String fName) throws CSUtilException {
        final Workbook book = fName.endsWith(".xlsx") ? new XSSFWorkbook() :
                fName.endsWith(".xls") ? new HSSFWorkbook() : null;
        if (book == null) {
            throw new CSUtilException("Illegal argument for file " + fName);
        }

        final Font titleFont = book.createFont();
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        final CellStyle titleStyle = book.createCellStyle();
        titleStyle.setAlignment(CellStyle.ALIGN_CENTER);

        final CellStyle contentStyle = book.createCellStyle();
        contentStyle.setAlignment(CellStyle.ALIGN_CENTER);
        contentStyle.setWrapText(true);

        final CellStyle roomHeader = book.createCellStyle();
        roomHeader.setAlignment(CellStyle.ALIGN_CENTER);
        roomHeader.setFillBackgroundColor(HSSFColor.ORANGE.index);

        for (final Floor floor : house.getFloors()) {
            final Sheet floorSheet = book.createSheet(floor.getName());
            final Row titleRow = floorSheet.createRow(0);
            for (int index = 0; index < TITLE.length; index++) {
                final Cell cell = titleRow.createCell(index);
                cell.setCellStyle(titleStyle);
                cell.setCellValue(TITLE[index]);
            }

            try {
                final Map<Room, MerchandiseInfo> roomMerchandiseInfoMap = floor.getFloorMerchandiseInfo();
                for (final Room room : roomMerchandiseInfoMap.keySet()) {
                    createMerchandiseBlock(room, roomMerchandiseInfoMap.get(room), floorSheet, roomHeader, contentStyle);
                }
            } catch (Exception ex) {
                throw new CSUtilException(ex);
            }
        }

        try {
            final FileOutputStream out = new FileOutputStream(new File(fName));
            book.write(out);
            out.close();
        } catch (Exception ex) {
            throw new CSUtilException(ex);
        }
    }


    private static void createMerchandiseBlock(final Room room, final MerchandiseInfo merchandiseInfo,
                                               final Sheet sheet, final CellStyle roomHeader, final CellStyle cellStyle){
        sheet.createRow(sheet.getLastRowNum() + 1);
        final Row header = sheet.createRow(sheet.getLastRowNum() + 1);
        final int firstMerchandiseRowIndex = header.getRowNum() + 1;
        for(int index = 0; index < TITLE.length; index++){
            final Cell cell = header.createCell(index);
            if(index == 0){
                cell.setCellValue(room == null ? "其他" :room.getRoomName());
            }
            cell.setCellStyle(roomHeader);
        }
        final Map<Merchandise, Double> quantityCounter = merchandiseInfo.getQuantityCounter();


        for(final Merchandise merchandise : quantityCounter.keySet()){
            final Row merchandiseRow = sheet.createRow(sheet.getLastRowNum() + 1);
            merchandiseRow.createCell(0).setCellStyle(cellStyle);
            merchandiseRow.getCell(0).setCellType(Cell.CELL_TYPE_BLANK);
            merchandiseRow.createCell(1).setCellValue(merchandise.getName());
            merchandiseRow.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
            merchandiseRow.getCell(1).setCellStyle(cellStyle);
            merchandiseRow.createCell(2).setCellValue(merchandise.getUnitName());
            merchandiseRow.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
            merchandiseRow.getCell(2).setCellStyle(cellStyle);
            merchandiseRow.createCell(3).setCellValue(merchandise.getUnitPrice());
            merchandiseRow.getCell(3).setCellType(Cell.CELL_TYPE_NUMERIC);
            merchandiseRow.getCell(3).setCellStyle(cellStyle);
            merchandiseRow.createCell(4).setCellValue(quantityCounter.get(merchandise).doubleValue());
            merchandiseRow.getCell(4).setCellType(Cell.CELL_TYPE_NUMERIC);
            merchandiseRow.getCell(4).setCellStyle(cellStyle);
            merchandiseRow.createCell(5).setCellType(Cell.CELL_TYPE_FORMULA);
            merchandiseRow.getCell(5).setCellStyle(cellStyle);
            merchandiseRow.getCell(5).setCellFormula("D"+(merchandiseRow.getRowNum() + 1)+
                    "*E"+(merchandiseRow.getRowNum() + 1));
        }

        final Row tailer = sheet.createRow(sheet.getLastRowNum() + 1);
        tailer.createCell(0).setCellType(Cell.CELL_TYPE_STRING);
        tailer.getCell(0).setCellValue("单间总计");
        tailer.getCell(0).setCellStyle(cellStyle);

        tailer.createCell(5).setCellType(Cell.CELL_TYPE_FORMULA);
        tailer.getCell(5).setCellStyle(cellStyle);
        tailer.getCell(5).setCellFormula("SUM(F"+(firstMerchandiseRowIndex+1)+":F"+tailer.getRowNum()+")");
        sheet.createRow(sheet.getLastRowNum() + 1);
    }

    public static void main(String[] args) throws CSUtilException {
        final String fName ="/home/gang-liu/Desktop/test.xls";
        final House house = new House();
        house.addFloor("麻木", new Floor());
        toExcel(house, fName);
    }

}
