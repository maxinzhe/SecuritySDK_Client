package com.sklois.demo.test;

import java.io.File;
import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.ccit.security.sdk.clientDemo.R;
import com.sklois.demo.util.Base64;
import com.sklois.haiyunKms.DownloadProgressListener;
import com.sklois.haiyunKms.FileDownloader;
import com.sklois.haiyunKms.SoftLibs;
import com.sklois.util.FileUtil;

public class CertAct extends FinalActivity {

	@ViewInject(id = R.id.init, click = "onClick")
	Button init;
	@ViewInject(id = R.id.btn_getcert)
	Button btn_getcert;

	@ViewInject(id = R.id.et_cert)
	EditText et_cert;

	@ViewInject(id = R.id.btn_alcert1)
	Button btn_alcert1;
	
	@ViewInject(id = R.id.btn_alcert2)
	Button btn_alcert2;
	
	@ViewInject(id = R.id.btn_alcert3)
	Button btn_alcert3;
	
	@ViewInject(id = R.id.btn_alcert4)
	Button btn_alcert4;
	
	@ViewInject(id = R.id.btn_deletecert)
	Button btn_deletecert;
	
	@ViewInject(id = R.id.btn_revokecert)
	Button btn_revokecert;
	
	@ViewInject(id = R.id.btn_downloadCRL)
	Button btn_downloadCRL;
	

	@ViewInject(id = R.id.tv_sub)
	TextView tv_sub;

	@ViewInject(id=R.id.rg_use)
	RadioGroup rg_use;

	@ViewInject(id=R.id.RadioButtonExchage)
	RadioButton rb_exchange;

	@ViewInject (id=R.id.RadioButtonSign)
		RadioButton rb_sign;

	//@ViewInject(id = R.id.tv_en)
	//TextView tv_en;

	//@ViewInject(id = R.id.tv_cn)
	//TextView tv_cn;

	//@ViewInject(id = R.id.tv_time)
	//TextView tv_time;

	// 算法
	//int algorithm = 0x00020402;
	
	String strDeviceID;
	TelephonyManager tm;
	

	private ProgressBar progressBar;
	private TextView resultView;


	int strCertUsage = 0x00020402;//待修改
	String ID = "testusr";//待修改
	/**
		 * 参数类型：因为启动一个线程还需要使用到上面方法的参数，而主方法启动后很快就会销毁， 那么使用Final可以解决参数丢失的问题 path
		 * 注意是Final类型 savedir 注意是Final类型
	 */
	private void download(final String path, final File savedir) {
			// 这里开启一个线程避免ANR错误
			new Thread(new Runnable() {
				@Override
				public void run() {
					FileDownloader loader = new FileDownloader(CertAct.this,
							path, savedir, 3);
					// 设置进度条的最大刻度为文件的长度
					progressBar.setMax(loader.getFileSize());
					try {
						loader.download(new DownloadProgressListener() {
							/**
							 * 注意这里的设计，显示进度条数据需要使用Handler来处理 因为非UI线程更新后的数据不能被刷新
							 */
							@Override
							public void onDownloadSize(int size) {
								// 实时获知文件已经下载的数据长度
								Message msg = new Message();
								// 设置消息标签
								msg.what = 1;
								msg.getData().putInt("size", size);
								// 使用Handler对象发送消息
								handler.sendMessage(msg);
							}
						});
					} catch (Exception e) {
						// 发送一个空消息到消息队列
						handler.obtainMessage(-1).sendToTarget();
						/**
						 * 或者使用下面的方法发送一个空消息 Message msg = new Message(); msg.what =
						 * 1; handler.sendMessage(msg);
						 */
					}
				}
			}).start();
			}
			
		/**
		 * Handler原理：当Handler被创建时会关联到创建它的当前线程的消息队列，该类用于往消息队列发送消息
		 * 
		 * 消息队列中的消息由当前线程内部进行处理
		 */
		private Handler handler = new Handler() {
			// 重写Handler里面的handleMessage方法处理消息
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					// 进度条显示
					progressBar.setProgress(msg.getData().getInt("size"));
					float num = (float) progressBar.getProgress()
							/ (float) progressBar.getMax();
					int result = (int) (num * 100);
					resultView.setText(result + "%");
					// 判断是否下载成功
					if (progressBar.getProgress() == progressBar.getMax()) {
						Toast.makeText(CertAct.this, "crl download success", Toast.LENGTH_SHORT)
								.show();
					}
					break;
				case -1:
					Toast.makeText(CertAct.this, "crl download error", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cert);
		
		resultView = (TextView) this.findViewById(R.id.result);
		
		progressBar = (ProgressBar) this.findViewById(R.id.downloadbar);
				

		tm = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);	/*
		 * 唯一的设备ID： GSM手机的 IMEI 和 CDMA手机的 MEID. Return null if device ID is not
		 * available.
		 */
		strDeviceID = tm.getDeviceId();// String


		init.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent=new Intent();
				intent.setClass(CertAct.this, InitAct.class);
				startActivity(intent);
			}
		});
		//if (id == init.getId()) {
		//} else
		rg_use.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				final int rb_exId=rb_exchange.getId();
//				int rb_siId=rb_sign.getId();

				switch(checkedId){
					case R.id.RadioButtonSign:
						strCertUsage=0x00020401;
						break;
					case R.id.RadioButtonExchage:
						strCertUsage=0x00020402;
						break;
				}
				/*
				 * #define SGD_KEYUSAGE_SIGN 0x00020401 签名证书 #define
				 * SGD_KEYUSAGE_KEYEXCHANGE 0x00020402 加密证书
				 */
			}
		});
		et_cert.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				Log.i("test", "完成编辑中给ID赋值" + et_cert.getText().toString());
				if (et_cert.getText().length() > 0) {

					ID = et_cert.getText().toString();
				}
				return false;
			}
		});
		et_cert.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {

				Log.i("test", "完成编辑中给ID赋值" + et_cert.getText().toString());
				if (et_cert.getText().length() > 0) {

					ID = et_cert.getText().toString();
				}
			}
		});
		btn_getcert.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				//int strCertUsage = 0x00020402;//待修改
				//String ID = "testusr";//待修改

				String LocalCertData = SoftLibs.getInstance().ReadLocalCert(ID,
						strCertUsage);
				/*
					byte[] cert = sdk.getCert(BaseUtil.appId, BaseUtil.bussinessId,
						Constant.CERT_TYPE_ENCRYPT);
				 * */
				if (LocalCertData != null) {
					//et_cert.setText(new String(cert));
					et_cert.setText(new String(LocalCertData));
				} else {
					Toast.makeText(CertAct.this,
							"获取证书失败", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		btn_deletecert.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				boolean bRet = false;
				String ID = "testusr";

				bRet = SoftLibs.getInstance().deleteLocalData(ID);

				if (bRet == true) {
					Toast.makeText(CertAct.this,
							"已删除", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(CertAct.this,
							"删除失败", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		btn_revokecert.setOnClickListener(new OnClickListener() {//吊销证书
			@Override
			public void onClick(View v) {
				
				boolean bRet = false;
				String ID = "testusr";//待修改
				/*
				 * #define SGD_KEYUSAGE_SIGN 0x00020401 签名证书 #define
				 * SGD_KEYUSAGE_KEYEXCHANGE 0x00020402 加密证书
				 */
				//int strCertUsage = 0x00020402;//待修改

				String LocalCertData1 = SoftLibs.getInstance().ReadLocalCert(ID,
						strCertUsage);
				
				bRet =  SoftLibs.getInstance().revokeCert(LocalCertData1);
				if (bRet == true) {
					Toast.makeText(CertAct.this,
							"已吊销", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(CertAct.this,
							"吊销失败，该证书已吊销过", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		
		btn_downloadCRL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			//	String path = "http://139.196.19.221:80/demo/1.crl";

					String path = "http://192.168.252.1:8080/CA/1.crl";
				// 判断SDCard是否存在
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// 下载操作
					download(path, Environment.getExternalStorageDirectory());
				} else {
					Toast.makeText(CertAct.this, "未找到SD卡",Toast.LENGTH_SHORT )
							.show();
				}
			}			
		});
		
		
		btn_alcert1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				//int strCertUsage = 0x00020402;
				//String ID = "testusr";

				String LocalCertData = SoftLibs.getInstance().ReadLocalCert(ID,
						strCertUsage);
				
				boolean bRet1 = false;
				boolean bRet2 = false;
				boolean bRet3 = false;
				
				if (LocalCertData != null) {
					/*
					 * 唯一的设备ID： GSM手机的 IMEI 和 CDMA手机的 MEID. Return null if device ID is not
					 * available.
					 */
					String strDeviceID = tm.getDeviceId();// String

					bRet1 = SoftLibs.getInstance().VerifyDevieAndLocalCert(ID, strCertUsage,
							strDeviceID);
					
					if (bRet1 == true) {
						Toast.makeText(CertAct.this,
								"设备绑定验证通过！" , Toast.LENGTH_SHORT).show();			

					} else {
						Toast.makeText(CertAct.this,
								"设备绑定验证不通过！" , Toast.LENGTH_SHORT).show();
					}
					
				} else {
					Toast.makeText(CertAct.this,
							"读取本地证书失败，错误码：" , Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		btn_alcert2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				//int strCertUsage = 0x00020402;
				//String ID = "testusr";

				String LocalCertData = SoftLibs.getInstance().ReadLocalCert(ID,
						strCertUsage);
				
				boolean bRet1 = false;
				boolean bRet2 = false;
				boolean bRet3 = false;
				
				if (LocalCertData != null) {

					//这里应该写成读取根证书文件，而根证书提前放置在apk目录中。
					//String IssuerCertData = "MIIBYTCCAQSgAwIBAgIOAiE0oira8IwG2wCdBwgwDAYIKoEcz1UBg3UFADAgMQswCQYDVQQGEwJDTjERMA8GA1UEAwwIU00yIFRlc3QwHhcNMTUxMTExMDYzOTQ0WhcNMjAxMTExMDYzOTQ0WjAgMQswCQYDVQQGEwJDTjERMA8GA1UEAwwIU00yIFRlc3QwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAQlssSfWplIsgRLnJ//doKF7B9ocLpoAihYU0Z9gf2yDx4S0j0Htw5914NpVTJQHBAGCbhlVzdg6Vjf5ZgZ2iK0oyAwHjAPBgNVHRMBAf8EBTADAQH/MAsGA1UdDwQEAwIBBjAMBggqgRzPVQGDdQUAA0kAMEYCIQDLi/HKdPM8im96Rj8GTQAMbrFN+m1lfqPjNkKurst5FwIhAOGmSoIP6Xbw7UEKf4GwhhNjyHjRFf9/CzRKmcx+QPCO";


					// 建议使用UTF-8和Unix换行符。
					// 文件路径为相对路径，比如：资源文件位于assets/smiley/sample.txt,则打开文件时，
					// getResources().getAssets().open("smiley/sample.txt")
					//String certPath="rootCert/root.cer";
					//String certPath="rootCert/srca.cer";
					String certPath="rootCert/son.cer";
					FileUtil fileUtil=new FileUtil(CertAct.this,certPath);
					String IssuerCertData = fileUtil.getCertString();
					Log.i("test","read the root Cert is:"+IssuerCertData);

					Log.i("test","Now try to get the public key of the cert");
					try{

						String publicKeyString=fileUtil.getPublicKey();
						Log.i("test","publicKey is:"+publicKeyString);
					}catch (Exception e){
						e.printStackTrace();
					}

					bRet2 =  SoftLibs.getInstance().VerifyCertByIssuerCert(LocalCertData, IssuerCertData);
					if (bRet2 == true) {
						Toast.makeText(CertAct.this,
								"根证书验证通过！" , Toast.LENGTH_SHORT).show();			

					} else {
						Toast.makeText(CertAct.this,
								"根证书验证不通过！" , Toast.LENGTH_SHORT).show();
					}					
					
				} else {
					Toast.makeText(CertAct.this,
							"读取本地待验证的证书失败，错误码：" , Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		btn_alcert3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				//int strCertUsage = 0x00020402;
				//String ID = "testusr";

				String LocalCertData = SoftLibs.getInstance().ReadLocalCert(ID,
						strCertUsage);
				
				boolean bRet1 = false;
				boolean bRet2 = false;
				boolean bRet3 = false;
				
				if (LocalCertData != null) {					
					
					String filePath = "/sdcard/1.crl";
					bRet3 = SoftLibs.getInstance().VerifyLocalCert(ID, strCertUsage,
							filePath);
					
					if (bRet3 == true) {
						Toast.makeText(CertAct.this,
								"证书在CRL列表中！该证书过期" , Toast.LENGTH_SHORT).show();

					} else {
						Toast.makeText(CertAct.this,
								"证书不在CRL列表中！该证书离线验证结果是可以使用" , Toast.LENGTH_SHORT).show();
					}								
					
				} else {
					Toast.makeText(CertAct.this,
							"读取本地证书失败，错误码：" , Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		btn_alcert4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//int strCertUsage = 0x00020402;
				//String ID = "testusr";

				String LocalCertData = SoftLibs.getInstance().ReadLocalCert(ID,
						strCertUsage);

				boolean bRet1 = false;
				boolean bRet2 = false;
				boolean bRet3 = false;

				if (LocalCertData != null) {

					bRet2 = SoftLibs.getInstance().ocspStatusCert(LocalCertData);
					if (bRet2 == true) {
						Toast.makeText(CertAct.this,
								"OCSP验证,已吊销！", Toast.LENGTH_SHORT).show();

					} else {
						Toast.makeText(CertAct.this,
								"OCSP验证，未吊销！", Toast.LENGTH_SHORT).show();
					}


				} else {
					Toast.makeText(CertAct.this,
							"读取本地待验证的证书失败，错误码：", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
