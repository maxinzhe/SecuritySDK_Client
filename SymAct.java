package com.sklois.demo.test;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.sklois.haiyunKms.SoftLibs;
import com.sklois.demo.util.Base64;
import com.ccit.security.sdk.clientDemo.R;

public class SymAct extends FinalActivity {
	@ViewInject(id = R.id.rg_sym)
	RadioGroup rg_sym;
	
	@ViewInject(id = R.id.radio_des)
	RadioButton radio_des;
	
	@ViewInject(id = R.id.radio_aes)
	RadioButton radio_aes;
	
	@ViewInject(id = R.id.radio_3des)
	RadioButton radio_3des;
	
	@ViewInject(id = R.id.radio_sm4)
	RadioButton radio_sm4;
	
	@ViewInject(id = R.id.et_symkey)
	EditText et_symkey;
	@ViewInject(id = R.id.et_src)
	EditText et_src;
	@ViewInject(id = R.id.et_encryptrs)
	EditText et_encryptrs;
	@ViewInject(id = R.id.et_decryptrs)
	EditText et_decryptrs;
	@ViewInject(id = R.id.btn_decrypt)
	Button btn_decrypt;
	@ViewInject(id = R.id.btn_encrypt)
	Button btn_encrypt;
	@ViewInject(id = R.id.tv_rs)
	TextView tv_rs;

	// 算法
	int algorithm = 203;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sym);
		rg_sym.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			/*
			 * 
			 * #define SYM_3DES 201 #define SYM_SM4 202 #define SYM_DES 203
			 */
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == radio_des.getId()) {
					algorithm = 203;
				} else if (checkedId == radio_3des.getId()) {
					algorithm = 201;
				} else if (checkedId == radio_aes.getId()) {
					algorithm = 204;
				}else{
					algorithm = 202;
				}
			}
		});
		btn_encrypt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String src = et_src.getText().toString();
				String symkey = et_symkey.getText().toString() + "11111111111111112222222211111111";
				if (src.equals("") || symkey.equals("")) {
					tv_rs.setText("请输入加密原文以及对称密钥");
				} else {
					byte[] rs = SoftLibs.getInstance().SymEncrypt(algorithm, symkey,
							src.getBytes());
					
					if (rs != null) {
						et_encryptrs.setText(Base64.encodeToString(rs,
								Base64.DEFAULT));
					} else {
						et_encryptrs.setText("");
						tv_rs.setText("对称加密失败");
					}
				}
			}
		});
		btn_decrypt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String symkey = et_symkey.getText().toString() + "11111111111111112222222211111111";
				String encryptData = et_encryptrs.getText().toString();
				
				//encryptData = encryptData.replaceAll("\r|\n", "");
				
				byte[] en = Base64.decode(encryptData.getBytes(),
						Base64.DEFAULT);
				
				if (encryptData.equals("") || symkey.equals("")) {
					tv_rs.setText("请输入解密密文以及对称密钥");
				} else {
					try {
						byte[] rs  = SoftLibs.getInstance().SymDecrypt(algorithm, symkey,
								en);
						
						if (rs != null) {
							et_decryptrs.setText(new String(rs));
						} else {
							tv_rs.setText("对称解密失败");
						}
					} catch (Exception e) {
						tv_rs.setText("对称解密失败,密文解base64失败");
					}
				}
			}
		});
	}
}
