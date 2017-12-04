package org.dualcom.xai;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.dualcom.xai.MyClass.*;

public class ExamActivity extends Activity {

    public TextView textView;
    public int columnSaveIndex;

    private String LOG_TAG = "ReadExcelFromUrl";
    ArrayList<ExamCart> examCartList;
    ArrayList<ExamGroups> examGroupsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        textView = (TextView) findViewById(R.id.textView);

        //URL path for the Excel file
        String url = "http://rapoo.mysit.ru/data/3.xls";
        excelURL(url);
    }

    private void displayCart() {

        String text = "";

        //Array list of countries
        List<String> myList = new ArrayList<String>();
        for(int i = 0, l = examGroupsList.size(); i < l; i++){ //examCartList.size()
            ExamGroups examGroups = examGroupsList.get(i);
            String myData = "Группа: " + examGroups.getGroup() + "\n";
            myList.add(myData);
            System.out.println("My data..................."+ myData);
            text += myData+"\n";
        }

        textView.setText("Всего: "+ examGroupsList.size() +"\n" + text);

        //Display the Excel data in a ListView
        /*ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.data_list, myList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(dataAdapter);*/


    }

    public void excelURL(String url) {
        Log.v(LOG_TAG, url);
        new ExcelURL().execute(url);
    }

    private class ExcelURL extends AsyncTask<String, Void, String> {
        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();
        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content =  null;
        private ProgressDialog dialog = new ProgressDialog(ExamActivity.this);

        protected void onPreExecute() {
            dialog.setMessage("Getting your data... Please wait...");
            dialog.show();
        }

        protected String doInBackground(String... urls) {

            String URL = null;

            try {

                URL = urls[0];
                HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);

                HttpGet httpGet = new HttpGet(URL);
                response = httpclient.execute(httpGet);

                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    parseExcel(response.getEntity().getContent());
                } else{
                    //Closes the connection.
                    Log.w("HTTP1:",statusLine.getReasonPhrase());
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                Log.w("HTTP2:",e );
                content = e.getMessage();
                cancel(true);
            } catch (IOException e) {
                Log.w("HTTP3:",e );
                content = e.getMessage();
                cancel(true);
            }catch (Exception e) {
                Log.w("HTTP4:",e );
                content = e.getMessage();
                cancel(true);
            }

            return content;
        }

        protected void onCancelled() {
            dialog.dismiss();
            Toast toast = Toast.makeText(ExamActivity.this,
                    "Error connecting to Server", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();

        }

        protected void onPostExecute(String content) {
            dialog.dismiss();
            displayCart();
        }



        private void parseExcel(InputStream fis){

            examCartList = new ArrayList<ExamCart>();
            examGroupsList = new ArrayList<ExamGroups>();
            columnSaveIndex = 0;

            try{

                // Create a workbook using the Input Stream
                HSSFWorkbook myWorkBook = new HSSFWorkbook(fis);

                // Get the first sheet from workbook
                HSSFSheet mySheet = myWorkBook.getSheetAt(0);

                // We now need something to iterate through the cells
                Iterator<Row> rowIter = mySheet.rowIterator();
                while(rowIter.hasNext()){

                    HSSFRow myRow = (HSSFRow) rowIter.next();
                    // Skip the first 2 rows
                    if(myRow.getRowNum() != 5 && myRow.getRowNum() != 53) {
                        continue;
                    }

                    Iterator<Cell> cellIter = myRow.cellIterator();
                    while(cellIter.hasNext()){

                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        String cellValue = "";

                        if(myCell.getColumnIndex() < 1) {
                            continue;
                        }

                        ExamGroups examGroups = new ExamGroups();

                        // Check for cell Type
                        if(myCell.getCellType() == HSSFCell.CELL_TYPE_STRING){
                            cellValue = myCell.getStringCellValue();
                        }
                        else {
                            cellValue = String.valueOf(myCell.getNumericCellValue())+"";
                            //String s = cellValue.split(".")[0];
                        }

                        if(!cellValue.equals("Дата") && !cellValue.equals("0"))
                            examGroups.setGroup(cellValue);
                        else
                            continue;

                        // Just some log information
                        Log.v(LOG_TAG, cellValue);

                        examGroupsList.add(examGroups);

                    }



                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }

    }

}
