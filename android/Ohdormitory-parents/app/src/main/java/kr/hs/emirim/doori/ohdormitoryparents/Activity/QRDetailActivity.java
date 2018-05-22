package kr.hs.emirim.doori.ohdormitoryparents.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import kr.hs.emirim.doori.ohdormitoryparents.Model.StudentInfo;
import kr.hs.emirim.doori.ohdormitoryparents.R;

public class QRDetailActivity extends AppCompatActivity {


    private final String TAG = "QRDetailActivity";

    private final String PUT_STUDENT_INFO = "STUDENT_ITEM";

    private StudentInfo studentInfo;


    TextView textView;
    ImageView qrImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        studentInfo = (StudentInfo) bundle.getSerializable(PUT_STUDENT_INFO);


        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String phone_number = pref.getString("phone_number", "");

        textView=(TextView)findViewById(R.id.text_none_qrcode);
        qrImage=(ImageView) findViewById(R.id.iv_generated_qrcode);

        generateQRCode(studentInfo.getNotice_id()+"/"+studentInfo.getEmirim_id()+"/"+phone_number);

//        FirebaseMessaging.getInstance().subscribeToTopic("parentNotice");
//

//
    }


    public void generateQRCode(String contents) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            Bitmap bitmap = toBitmap(qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE, 150, 150));
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap toBitmap(BitMatrix matrix) {
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }
}
