package com.sklois.demo.test;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ccit.security.sdk.clientDemo.R;
import com.sklois.haiyunKms.SoftLibs;


public class InitAct extends FinalActivity {
	@ViewInject(id = R.id.algorithm)
	RadioGroup algorithm_rg;

	@ViewInject(id = R.id.radio_sm2)
	RadioButton radio_sm2;

	@ViewInject(id = R.id.radio_rsa)
	RadioButton radio_rsa_sha1;

	@ViewInject(id = R.id.radio_rsa_1)
	RadioButton radio_rsa_sha256;
	
	@ViewInject(id = R.id.radio_rsa_111)
	RadioButton radio_rsa2048_sha1;

	@ViewInject(id = R.id.radio_rsa_11)
	RadioButton radio_rsa2048_sha256;
	
	
	@ViewInject(id = R.id.btn_pin_decrypt)
	Button btn_pin_decrypt;

	@ViewInject(id = R.id.tv_init1)
	TextView tv_init1;

	@ViewInject(id=R.id.checkBox)
	CheckBox checkBox;

//EditText of the table list

	@ViewInject(id=R.id.CNeditText)
			EditText CNeditText;

	@ViewInject(id=R.id.OeditText4)
			EditText OeditText4;

	@ViewInject(id=R.id.OUeditText5)
			EditText OUeditText5;

	@ViewInject(id=R.id.LeditText3)
			EditText LeditText3;
	@ViewInject(id=R.id.STeditText2)
			EditText STeditText2;
	@ViewInject(id=R.id.EmaileditText6)
			EditText EmaileditText6;
	@ViewInject(id=R.id.ExtendeditText7)
			EditText ExtendedtitText7;
	String strDeviceID;

	
	// 算法
	int algorithm = 0x00020101;
	//SDKImpl sdk;
	
	

	String pin = "12345678";
	boolean isBindSimCard = false;
	String serviceURL = "124.207.215.118:7080";
	// String serviceURL = "192.168.100.46:8080";
	int timeOut = 60000;
	boolean testFlag = true;

	String message = "";
	String id = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.init);
		
		/*
		 * 唯一的设备ID： 可以是手机的 IMEI. Return null if device ID is not
		 * available.
		 */
		TelephonyManager tm = (TelephonyManager) this
					.getSystemService(TELEPHONY_SERVICE);
		strDeviceID = tm.getDeviceId();// String
	
		algorithm_rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			/*
			 * 
			 * //签名算法标识 #define SGD_SHA1_RSA 0x00001002 
			 * #define SGD_SHA256_RSA 0x00001004 
			 * #define SGD_SM3_SM2 0x00020101
			 * #define SGD_SHA1_RSA_2048	0x00001006
			 * #define SGD_SHA256_RSA_2048	0x00001008
			 * #define SGD_SM3_SM2			0x00020101
			 */
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == radio_rsa_sha1.getId()) {
					algorithm = 0x00001002;//0x00001002;
				} else if (checkedId == radio_rsa_sha256.getId()){
					algorithm = 0x00001004;//0x00001004;
				}else if (checkedId == radio_rsa2048_sha1.getId()){
					algorithm = 0x00001006;//0x00001004;
				}else if (checkedId == radio_rsa2048_sha256.getId()){
					algorithm = 0x00001008;//0x00001004;
				}else{
					algorithm = 0x00020101;
				}
			}
		});


		btn_pin_decrypt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				int ret = 0;
				
				int ASymAlgo = algorithm;
				String pin = "12345678";
				String ID = "testusr";



				String ST = "BEIJING"; // USER province
				String L = "BEIJING"; // USER city
				String O = "SKLOIS";// USER company
				String OU = "YISHI";// USER department
				String Email = "XXX@QQ.COM";// USER email
				String certType = "2"; // 1 单证书 2双证书
				String notBefore = "2015-11-11 12:00:00";
				String notAfter = "2016-11-01 12:00:00";
				String extend = "1.1.1.1.1:" + strDeviceID; // 为了与终端设备绑定，将终端号签到证书拓展项

				Log.i("test","CNedit.getText.length"+CNeditText.getText().toString().trim().length());
				if((!checkBox.isChecked())&&(CNeditText.getText().toString().trim().length()>0)){
					try {
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						Date curDate = new Date(System.currentTimeMillis());
						String time = format.format(curDate);
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(curDate);
						calendar.add(Calendar.YEAR, +1);
						String endDate = format.format(calendar.getTime());


						ID = CNeditText.getText().toString().trim();
						ST = STeditText2.getText().toString().trim();
						L = LeditText3.getText().toString().trim();
						O = OeditText4.getText().toString().trim();
						OU = OUeditText5.getText().toString().trim();
						Email = EmaileditText6.getText().toString().trim();

						notBefore = time;
						notAfter = endDate;
					}catch (Exception e){
						e.printStackTrace();
					}

				}
				/*
				 * 
				 * //签名算法标识 #define SGD_SHA1_RSA 0x00001002 #define SGD_SHA256_RSA
				 * 0x00001004 #define SGD_SM3_SM2 0x00020101
				 */


				byte[] pubkey = SoftLibs.getInstance().GenKeyPair(ASymAlgo, ID, pin);
				String strPubkey = Base64.encodeToString(pubkey, Base64.DEFAULT);

				boolean bret = true;
				bret = SoftLibs.getInstance().ApplyCert(ASymAlgo, ID, strPubkey, ST, L, O,
						OU, Email, certType, notBefore, notAfter, extend);				
				tv_init1.setText("申请证书结果：" + bret);
				
			}
		});
		
	}
}
