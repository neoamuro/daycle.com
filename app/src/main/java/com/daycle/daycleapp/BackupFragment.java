package com.daycle.daycleapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.daycle.daycleapp.adapters.CustomDateListViewAdapter;
import com.daycle.daycleapp.adapters.DefaultDateArrayAdapterModel;
import com.daycle.daycleapp.applications.App;
import com.daycle.daycleapp.models.ActionBarModel;
import com.daycle.daycleapp.models.AttendanceDayModel;
import com.daycle.daycleapp.models.AttendanceModel;
import com.daycle.daycleapp.models.BackupModel;
import com.daycle.daycleapp.utils.L;
import com.daycle.daycleapp.utils.NetworkUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BackupFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // 프래그먼트 메인 뷰 리소스 아이디
    private final int FRAGMENT_MAIN_VIEW_RES_ID = R.layout.fragment_backup;
    final int RESOLVE_CONNECTION_REQUEST_CODE = 1;
    final String BACKUP_FILE_NAME = "com.daycle.daycleapp_backup.json";
    GoogleApiClient mGoogleApiClient;
    CustomDateListViewAdapter adapter;
    ListView listView;

    private interface CallbackBackupFile {
        void getDriveId(DriveId driveId);
    }

    private CallbackBackupFile callbackBackupFile;

    public BackupFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        setLayout(inflater, container, FRAGMENT_MAIN_VIEW_RES_ID);

        // 액션바 설정
        ActionBarModel actionBarModel = new ActionBarModel(getString(R.string.menu_backup));
        actionBarModel.backgroundColorResId = R.color.colorPreference;
        fragmentCallback.setActionBar(actionBarModel);

        // UI 인스턴스
        listView = (ListView)mainView.findViewById(R.id.listView);

        // 구글 api 초기화
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        // 백업 UI 리스트 초기화
        ArrayList<DefaultDateArrayAdapterModel> items = new ArrayList<>();
        items.add(new DefaultDateArrayAdapterModel("B", getString(R.string.backup_item01)));
        items.add(new DefaultDateArrayAdapterModel("R", getString(R.string.backup_item02)));
        adapter = new CustomDateListViewAdapter(getContext(), items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // 네트워크가 연결되어 있지 않으면 백
                boolean availableNetwork = NetworkUtil.CheckNetwork(getContext());
                if(!availableNetwork){
                    onBack();
                    return;
                }

                if(!mGoogleApiClient.isConnected()){
                    //L.d("접속 안되어 있음.");
                    //App.showToast("Please Reconnect.");

                    // 접속 시도
                    mGoogleApiClient.connect();
                    return;
                }

                DefaultDateArrayAdapterModel item = (DefaultDateArrayAdapterModel)adapterView.getItemAtPosition(i);
                if(item.code.equals("B")){

                    // 백업한 파일이 있는지 확인
                    getBackupFile(new CallbackBackupFile() {
                        @Override
                        public void getDriveId(DriveId driveId) {

                            // 드라이브 아이디가 없으면 백업파일이 없는 것임
                            if(driveId == null){
                                // 드라이브 컨텐츠를 생성한다.
                                // 구글 드라이브 접속 준비?
                                // 새로 파일을 쓸 준비를 함
                                Drive.DriveApi.newDriveContents(mGoogleApiClient)
                                        .setResultCallback(driveContentsCallback);
                            }else{

                                // 파일이 있다면 삭제함
                                DriveFile file = driveId.asDriveFile();
                                PendingResult<Status> result = file.delete(mGoogleApiClient);
                                result.setResultCallback(new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(@NonNull Status status) {

                                        // 파일 삭제에 성공하면 파일 새로 쓸 준비함
                                        if(status.isSuccess()){
                                            // 드라이브 컨텐츠를 생성한다.
                                            // 구글 드라이브 접속 준비?
                                            Drive.DriveApi.newDriveContents(mGoogleApiClient)
                                                    .setResultCallback(driveContentsCallback);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
                // 복구
                else if(item.code.equals("R")){
                    getBackupFile(new CallbackBackupFile() {
                        @Override
                        public void getDriveId(final DriveId driveId) {
                            if(driveId == null){
                                App.showToast(getString(R.string.backup_nohave_item));
                            }else{

                                App.confirm(getString(R.string.backup_item_confirm), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        App.showProgressBar();

                                        // 백업한 파일이 있다면 복구한다.
                                        DriveFile file = driveId.asDriveFile();
                                        file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, new DriveFile.DownloadProgressListener() {

                                            @Override
                                            public void onProgress(long progress, long total) {}
                                        }).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                                            @Override
                                            public void onResult(@NonNull final DriveApi.DriveContentsResult driveContentsResult) {


                                                // 가져오기 실패
                                                if(!driveContentsResult.getStatus().isSuccess()){
                                                    App.hideProgressBar();
                                                    App.showToast(driveContentsResult.getStatus().getStatusMessage());
                                                    return;
                                                }

                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        // 파일을 성공적으로 가져왔음
                                                        InputStream stream = driveContentsResult.getDriveContents().getInputStream();
                                                        try{

                                                            L.d("파일 데이터 읽어오기 준비");
                                                            // 파일 데이터를 읽어온다.
                                                            StringBuffer stringBuffer = new StringBuffer();
                                                            int len;
                                                            byte[] buffer = new byte[4096];
                                                            while ((len = stream.read(buffer))!= -1){
                                                                stringBuffer.append(new String(buffer, 0, len));
                                                            }

                                                            // 스트림 닫음
                                                            stream.close();

                                                            // 읽어온 json 파일을 모델로 변환
                                                            String json = stringBuffer.toString();

                                                            L.d("파일 데이터 읽어오기 완료");

                                                            Gson gson = new Gson();
                                                            BackupModel backupData = gson.fromJson(json, BackupModel.class);
                                                            //App.setSettings(getContext(), backupData.settings);

                                                            L.d("Gson 변환");

                                                            AttendanceModel.restore(backupData.attendance_items);

                                                            L.d("AttendanceModel 복구");

                                                            AttendanceDayModel.restore(backupData.attendance_day_items);

                                                            L.d("AttendanceDayModel 복구");

                                                            App.hideProgressBar();

                                                            App.showToast(getString(R.string.restore_completed));
                                                            // 복구 완료가 되면 메인 리스트로...
                                                            onBack();

                                                        } catch (IOException e) {
                                                            App.hideProgressBar();
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            }
                                        });

                                    } // public void onClick(DialogInterface dialogInterface, int i)

                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        return mainView;
    }

    // 백업한 파일 가져오기
    private void getBackupFile(final CallbackBackupFile callbackBackupFile){

        // 네트워크가 연결되어 있지 않으면 백
        boolean availableNetwork = NetworkUtil.CheckNetwork(getContext());
        if(!availableNetwork){
            onBack();
            return;
        }

        App.showProgressBar();

        // 드라이브 접속에 성공하면 바로 백업한 데이터가 있는지 알아온다.
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, BACKUP_FILE_NAME))
                .build();
        Drive.DriveApi.query(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(@NonNull DriveApi.MetadataBufferResult result) {

                        App.hideProgressBar();

                        if (!result.getStatus().isSuccess()) {
                            L.d("에러");
                            return;
                        }

                        MetadataBuffer buffer = result.getMetadataBuffer();
                        int cnt = buffer.getCount();

                        // 파일이 한개도 없다. (백업이 한번도 안됐음)
                        if(cnt == 0){
                            L.d("백업한 파일 없음");
                            if(callbackBackupFile != null)
                                callbackBackupFile.getDriveId(null);
                            return;
                        }

                        // 한개만 있는것으로 간주
                        Metadata metadata = buffer.get(0);
//                        L.d("metadata.isTrashed(): " + metadata.isTrashed());
//                        L.d("metadata.isEditable(): " + metadata.isEditable());
//                        L.d("metadata.getDriveId(): " + metadata.getDriveId());
//                        L.d("metadata.getWebContentLink(): " + metadata.getWebContentLink());
                        //L.d("파일 사이즈: " + metadata.getFileSize());
                        Date createdDate = metadata.getCreatedDate(); // 생성 날짜를 가져온다.

                        // 휴지통에 있는게 아닌 데이터를 가져온다.
                        if(metadata.isTrashed()){
                            if(callbackBackupFile != null)
                                callbackBackupFile.getDriveId(null);
                            return;
                        }

                        // 메타데이터를 가져와서 리스트의 백업 날짜를 업데이트한다.
                        View v = listView.getChildAt(0);
                        L.d(metadata.getTitle());
                        TextView itemDateTextView = (TextView)v.findViewById(R.id.itemDateTextView);
                        itemDateTextView.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm").format(createdDate));

                        if(callbackBackupFile != null)
                            callbackBackupFile.getDriveId(metadata.getDriveId());
                    }
                });
    }

    // 접속 준비 완료 콜백
    // 여기서 파일을 새로 만든다.
    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        App.showToast("Error while trying to create new file contents");
                        return;
                    }

                    // 사용자의 드라이브 컨텐츠 가져옴
                    final DriveContents driveContents = result.getDriveContents();

                    // Perform I/O off the UI thread.
                    new Thread() {
                        @Override
                        public void run() {

                            // 드라이브 컨텐츠에서 아웃풋 파일 스트림을 생성한다.
                            OutputStream outputStream = driveContents.getOutputStream();
//                            try {
//                                FileInputStream inputStream = new FileInputStream(BACKUP_FILE_NAME);
//
//                                try {
//                                    L.d("파일크기: " + inputStream.getChannel().size());
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                    L.d("파일읽기 실패");
//                                }
//
//                                byte[] buffer = new byte[1024];
//                                int len = 0;
//                                try {
//                                        while ((len = inputStream.read(buffer)) > 0){
//                                            outputStream.write(buffer, 0, len);
//                                        }
//
//
//
//
//                                    // 파일 닫기
//                                    inputStream.close();
//
//                                    // 드라이브 스트림에 데이터 플러시
//                                    outputStream.flush();
//
//                                    // 드라이브 스트림 닫기
//                                    outputStream.close();
//
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                    L.d("드라이브 스트림에 파일 쓰기 에러");
//                                }
//
//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                                L.d("파일 열기 에러");
//                            }



                            // 백업용 json data 만들기
                            BackupModel backupData = new BackupModel();
                            backupData.settings = App.settings; // 세팅 데이터
                            backupData.attendance_items = AttendanceModel.selectAll(AttendanceModel.SelectMode.ALL, false);
                            backupData.attendance_day_items = AttendanceDayModel.selectAll(0, AttendanceDayModel.SelectMode.ALL);
                            Gson gson = new Gson();
                            String json = gson.toJson(backupData);

                            // 파일 쓰기 스트림 생성
                            // json 데이터 쓰기
                            Writer writer = new OutputStreamWriter(outputStream);
                            try {
                                writer.write(json);
                                writer.close();
                            } catch (IOException e) {
                                L.d(e.getMessage());
                            }

                            // 파일에 대한 메타데이터
                            // mime type: 텍스트 파일
                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle(BACKUP_FILE_NAME)
                                    .setDescription("Daycle backup data")
                                    .setLastViewedByMeDate(new Date())
                                    .setStarred(false).build();

                            // 루트 폴더에 파일  쓰기
                            Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                    .createFile(mGoogleApiClient, changeSet, driveContents)
                                    .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                        @Override
                                        public void onResult(@NonNull DriveFolder.DriveFileResult driveFileResult) {
                                            if (!driveFileResult.getStatus().isSuccess()) {
                                                App.showToast("Error created backup data.");
                                                return;
                                            }

                                            // 파일 생성
                                            // 파일에 대한 고유 아이디 가져옴
                                            //App.showToast("Created a file with content id: " + result.getDriveFile().getDriveId());
                                            App.showToast(getString(R.string.backup_completed));

                                            DefaultDateArrayAdapterModel item = adapter.getItem(0);
                                            item.date = new Date();
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }.start();
                }
            };

    @Override
    public void onStart() {
        super.onStart();

        // 네트워크가 연결되어 있지 않으면 백
        boolean availableNetwork = NetworkUtil.CheckNetwork(getContext());
        if(availableNetwork == false){
            onBack();
            return;
        }

        App.showProgressBar();

        // 시작하면 구글 api에 접속한다.
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        L.d("커넥트 성공");

        App.hideProgressBar();

        //mGoogleApiClient.clearDefaultAccountAndReconnect();


        // 구글 드라이브가 싱크되기를 기다린다.
//        Drive.DriveApi.requestSync(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
//            @Override
//            public void onResult(@NonNull Status status) {
//                L.d("status.getStatusCode(): " + status.getStatusCode());
//                App.hideProgressBar();
//                if(status.getStatusCode() == DriveStatusCodes.DRIVE_RATE_LIMIT_EXCEEDED){
//                    L.d("갱신을 너무 자주 했음.");
//                }else{
//                    getBackupFile(null);
//                }
//            }
//        });

        // 백업 파일이 있는지 체크
        getBackupFile(null);
    }

    @Override
    public void onConnectionSuspended(int i) {
        App.hideProgressBar();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        App.hideProgressBar();

        // 한번도 접속을 하지 않았다면...??
        if (connectionResult.hasResolution()) {
            try {
                // 계정 연결을 시도한다.
                connectionResult.startResolutionForResult(getActivity(), RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
                L.d("커넥트 실패");
            }
        } else {
            //   GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
            L.d("커넥트 실패");
        }
    }

    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:

                // 계정 연결에 성공하면 바로 구글 드라이브 접속 시도
                if (resultCode == Activity.RESULT_OK) {
                    mGoogleApiClient.connect();
                    L.d("연결 성공");
                }else{
                    L.d("연결 실패");
                    //App.showToast("Fail Google Drive connected. resultCode: " + resultCode);
                    App.showToast("Fail Google Drive connected.");
                }
                break;
        }
    }
}
