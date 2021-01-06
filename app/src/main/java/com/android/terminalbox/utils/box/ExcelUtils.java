package com.android.terminalbox.utils.box;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

import com.android.terminalbox.core.bean.user.EpcFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelUtils {
    public static WritableFont arial14font = null;

    public static WritableCellFormat arial14format = null;
    public static WritableFont arial10font = null;
    public static WritableCellFormat arial10format = null;
    public static WritableFont arial12font = null;
    public static WritableCellFormat arial12format = null;

    public final static String UTF8_ENCODING = "UTF-8";
    public final static String GBK_ENCODING = "GBK";


    public static List<EpcFile> read2DB(File f, Context con) {
        ArrayList<EpcFile> billList = new ArrayList<>();
        try {
            Workbook course = null;
            course = Workbook.getWorkbook(f);
            Sheet sheet = course.getSheet(0);

            Cell cell = null;
            for (int i = 1; i < sheet.getRows(); i++) {
                EpcFile tc = new EpcFile();
                cell = sheet.getCell(0, i);
                tc.setAstCode(cell.getContents());
                cell = sheet.getCell(1, i);
                tc.setEpcCode(cell.getContents());
                cell = sheet.getCell(2, i);
                tc.setName(cell.getContents());
                billList.add(tc);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return billList;
    }


    public static void writeExcel(Context context, List<EpcFile> exportOrder,
                                  String fileName) throws Exception {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && getAvailableStorage() > 1000000) {
            Toast.makeText(context, "SD卡不可用", Toast.LENGTH_LONG).show();
            return;
        }

        //这些是你要导出的字段
        String[] title = {"资产编号","EPC","书名"};
        File file;
        File dir = new File(Environment.getExternalStorageDirectory() + "/新文件夹/");
        file = new File(dir, fileName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        // 创建Excel工作表
        WritableWorkbook wwb;
        OutputStream os = new FileOutputStream(file);
        wwb = Workbook.createWorkbook(os);
        // 添加第一个工作表并设置第一个Sheet的名字
        WritableSheet sheet = wwb.createSheet("a", 0);
        Label label;
        for (int i = 0; i < title.length; i++) {
            // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
            // 在Label对象的子对象中指明单元格的位置和内容
            label = new Label(i, 0, title[i], getHeader());
            // 将定义好的单元格添加到工作表中
            sheet.addCell(label);
        }
        //exportOrder就是你要导出的对应字段值
        for (int i = 0; i < exportOrder.size(); i++) {
            EpcFile order = exportOrder.get(i);
            Label astCodeLable = new Label(0, i + 1, order.getAstCode());
            Label epcLable = new Label(1, i + 1, order.getEpcCode());
            Label nameLabel = new Label(2, i + 1, order.getName());
            sheet.addCell(astCodeLable);
            sheet.addCell(epcLable);
            sheet.addCell(nameLabel);
        }
        // 写入数据
        wwb.write();
        // 关闭文件
        wwb.close();
        Toast.makeText(context, "写入成功", Toast.LENGTH_LONG).show();
        //QueryUser(new File(dir, "采矿权信息表" + ".xls"));
    }

    public static WritableCellFormat getHeader() {
        WritableFont font = new WritableFont(WritableFont.TIMES, 10,
                WritableFont.BOLD);// 定义字体
        try {
            font.setColour(Colour.BLUE);// 蓝色字体
        } catch (WriteException e1) {
            e1.printStackTrace();
        }
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.CENTRE);// 左右居中
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// 上下居中
            // format.setBorder(Border.ALL, BorderLineStyle.THIN,
            // Colour.BLACK);// 黑色边框
            // format.setBackground(Colour.YELLOW);// 黄色背景
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return format;
    }

    private static long getAvailableStorage() {

        StatFs statFs = new StatFs(root);
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        long availableSize = blockSize * availableBlocks;
        // Formatter.formatFileSize(context, availableSize);
        return availableSize;
    }

    public static String root = Environment.getExternalStorageDirectory()
            .getPath();

    //读取CSV文件
    public static List<EpcFile> readCSV(File file, Activity activity){
        List<EpcFile> list=new ArrayList<>();
        FileInputStream fiStream;
        Scanner scanner;
        try {
            fiStream=new FileInputStream(file);
            scanner=new Scanner(fiStream,"UTF-8");
            scanner.nextLine();//读下一行,把表头越过。不注释的话第一行数据就越过去了
            int a=0;
            while (scanner.hasNextLine()) {
                String sourceString = scanner.nextLine();
                Log.e("source-->", sourceString);
                Pattern pattern = Pattern.compile("[^,]*,");
                Matcher matcher = pattern.matcher(sourceString);
                String[] lines=new String[21];
                lines = sourceString.split(",");
                EpcFile fileBean = new EpcFile();
                fileBean.setAstCode(lines[0]);
                fileBean.setEpcCode(lines[1]);
                fileBean.setName(lines[2]);
                list.add(fileBean);
                a++;
                //i=0;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }
}