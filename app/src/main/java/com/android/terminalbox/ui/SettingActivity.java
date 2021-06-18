package com.android.terminalbox.ui;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.terminalbox.R;
import com.android.terminalbox.base.activity.BaseActivity;
import com.android.terminalbox.contract.SettingsContract;
import com.android.terminalbox.core.DataManager;
import com.android.terminalbox.core.bean.cmb.AssetFilterParameter;
import com.android.terminalbox.core.bean.user.EpcFile;
import com.android.terminalbox.core.room.BaseDb;
import com.android.terminalbox.presenter.SettingsPresenter;
import com.android.terminalbox.utils.StringUtils;
import com.android.terminalbox.utils.ToastUtils;
import com.android.terminalbox.utils.box.ExcelUtils;
import com.multilevel.treelist.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity<SettingsPresenter> implements SettingsContract.View {
    @BindView(R.id.title_content)
    TextView titleContent;
    @BindView(R.id.et_mixtime)
    EditText mixTime;
    @BindView(R.id.et_mix_unchange)
    EditText mixTimeUnchange;
    @BindView(R.id.et_ip_one)
    EditText ipOne;
    @BindView(R.id.et_ip_two)
    EditText ipTwo;
    @BindString(R.string.loc_id)
    String locId;
    @BindString(R.string.loc_name)
    String locName;
    private String ipRegex = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";
    private int currentPage = 1;
    private int pageSize = 600;
    private AssetFilterParameter conditions = new AssetFilterParameter();

    @Override
    public SettingsPresenter initPresenter() {
        return new SettingsPresenter();
    }

    @Override
    protected void initEventAndData() {
        titleContent.setText("设置");
        List<Node> mSelectAssetsLocations = new ArrayList<>();
        mSelectAssetsLocations.add(new Node(locId, "-1", locName));
        conditions.setmSelectAssetsLocations(mSelectAssetsLocations);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initToolbar() {

    }

    @OnClick({R.id.title_back, R.id.import_data, R.id.bt_mixtime, R.id.bt_mixtime_unchange, R.id.bt_ip_one, R.id.bt_ip_two, R.id.import_remote_data})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.import_data:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置任意类型
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
                break;
            case R.id.bt_mixtime:
                String s1 = mixTime.getText().toString();
                if (!StringUtils.isEmpty(s1)) {
                    DataManager.getInstance().saveMixTime(Integer.valueOf(s1));
                    ToastUtils.showShort("设置成功");
                } else {
                    ToastUtils.showShort("请输入正确的数字");
                }
                break;
            case R.id.bt_mixtime_unchange:
                String s2 = mixTimeUnchange.getText().toString();
                if (!StringUtils.isEmpty(s2)) {
                    DataManager.getInstance().saveMixTimeUnchange(Integer.valueOf(s2));
                    ToastUtils.showShort("设置成功");
                } else {
                    ToastUtils.showShort("请输入正确的数字");
                }
                break;
            case R.id.bt_ip_one:
                String ipOneStr = ipOne.getText().toString();
                if (!StringUtils.isEmpty(ipOneStr) && Pattern.matches(ipRegex, ipOneStr)) {
                    DataManager.getInstance().saveIpOne(ipOneStr);
                    ToastUtils.showShort("设置ip1成功,退出重启app生效");
                } else {
                    ToastUtils.showShort("请输入正确的ip地址");
                }
                break;
            case R.id.bt_ip_two:
                String ipTwoStr = ipTwo.getText().toString();
                if (!StringUtils.isEmpty(ipTwoStr) && Pattern.matches(ipRegex, ipTwoStr)) {
                    DataManager.getInstance().saveIpTwo(ipTwoStr);
                    ToastUtils.showShort("设置ip2成功,退出重启app生效");
                } else {
                    ToastUtils.showShort("请输入正确的ip地址");
                }
                break;
            case R.id.import_remote_data:
                mPresenter.fetchPageAssetsList(pageSize, currentPage, "", "", conditions.toString());
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                importExcle(data);
            }
        }
    }

    /**
     * 导入选中文件
     */
    public void importExcle(Intent data) {
        Uri uri = data.getData();
        if (uri != null) {
            String path = getPath(this, uri);
            if (path != null) {
                File file = new File(path);
                if (file.exists()) {
                    //获取的路径
                    String upLoadFilePath = file.toString();
                    //文件名
                    String upLoadFileName = file.getName();
                    String[] strArray = upLoadFileName.split("\\.");
                    int suffixIndex = strArray.length - 1;
                    File dir = new File(upLoadFilePath);
                    //调用查询方法
                    final List<EpcFile> fileBeans = new ArrayList<>();
                    if (upLoadFilePath.endsWith(".xls")) {
                        fileBeans.addAll(ExcelUtils.read2DB(dir, this));
                    } else if (upLoadFilePath.endsWith(".csv") || upLoadFilePath.endsWith(".txt")) {
                        fileBeans.addAll(ExcelUtils.readCSV(dir, this));
                    }
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            BaseDb.getInstance().getEpcFileDao().deleteAllData();
                            BaseDb.getInstance().getEpcFileDao().insertItems(fileBeans);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Toast.makeText(SettingActivity.this, "数据导入成功", Toast.LENGTH_SHORT).show();
                        }
                    }.execute();

                }
            }
        }
    }

    public String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"content***"+uri.toString());
            return getDataColumn(context, uri, null, null);
        }
        // Files
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"file***"+uri.toString());
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    public void handleFetchPageAssetsList(int resultSize) {
        ToastUtils.showShort("导入 " + resultSize + "  条数据！");
    }
}
