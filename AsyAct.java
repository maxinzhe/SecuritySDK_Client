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

import com.sklois.demo.util.Base64;
import com.sklois.haiyunKms.SoftLibs;
import com.ccit.security.sdk.clientDemo.R;

public class AsyAct extends FinalActivity {
	@ViewInject(id = R.id.rg_asy)
	RadioGroup rg_asy;

	@ViewInject(id = R.id.radio_sm2_asy)
	RadioButton radio_sm2;

	@ViewInject(id = R.id.radio_rsa_asy)
	RadioButton radio_rsa_asy;
	
	@ViewInject(id = R.id.radio_rsa_asy1)
	RadioButton radio_rsa_asy2048;

	@ViewInject(id = R.id.et_src)
	EditText et_src;

	@ViewInject(id = R.id.et_encryptrs)
	EditText et_encryptrs;

	@ViewInject(id = R.id.et_decryptrs)
	EditText et_decryptrs;

	@ViewInject(id = R.id.btn_encrypt)
	Button btn_encrypt;

	@ViewInject(id = R.id.btn_decrypt)
	Button btn_decrypt;

	@ViewInject(id = R.id.tv_sub)
	TextView tv_sub;

	// 算法
	int algorithm = 101;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.asy);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
				/*
		 * 
		 * #define ASYM_ALGO_RSA 101 #define ASY_ALGO_SM2 102
		 */
		rg_asy.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == radio_rsa_asy.getId()) {
					algorithm = 101;
				}if (checkedId == radio_rsa_asy2048.getId()) {
					algorithm = 1011;
				}else {
					algorithm = 102;
				}
			}
		});
		btn_encrypt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String input = et_src.getText().toString();
				
				byte[] rs;
				
				if (101 == algorithm) {
					String RSAPubkey = "MIGJAoGBAMZSRLicA6Ih7C/MYHBzvcomlXXOBWgP5WHnAOW+Ff+rv/VGy4qKLZdIB5qf2ySPbnNInswD3PYekXqwRIUryRsQnegRuVDhZ24I9C5KeDi51Che+M49/MLnDdfruegWin4JMGB3pzNS5anHRs55AiNgGHjWNNCPN1XhqNigPpjzAgMBAAE=";
					rs = SoftLibs.getInstance().EncryptByPubkey(algorithm,
							RSAPubkey, input.getBytes());
				}if (1011 == algorithm) {
					String RSAPubkey = "MIIBCgKCAQEAqcTudLDTg1pV1tMjGnP8Fm4bHMDzB/0jhvsP6KjbFwj+VdzWemSuDkovTR+7+Xkmc00qD/mpdhUDkPYqxDQt4O1bFaHv376Q5mfYPkSocUkWcIY2mFwOWLv1c1WIqgEdDEUEClsiRNwgKql3kuK+hZQJ+xvMZ372ktCAl2bwN3rDIdc4+CKh7AH2oYXttH6Is5ArwS9bBQnSDY1/WOiANYB76gJWjYZcP/ssq6PxTzgJXa6pderfdbvl2CoZPH5R1WAWvcyRpWu9qFId8TRcvdLH5W9QeRa1z++oBYdgtXx4jtUmU2Zp+Lf6kOnw3x8GUtJRchCaJdel4/0A2sllMQIDAQAB";
					rs = SoftLibs.getInstance().EncryptByPubkey(algorithm,
							RSAPubkey, input.getBytes());
				}  else {
					String SM2Pubkey = "Cwa48zT3XS9l2yd0ecJcqUKVmWtJinWh0ZLIwrdhDBCsmRjFfGANhPRYLvY4kUYqowa6sfseNUKgt7hbklcxMw==";
					rs = SoftLibs.getInstance().EncryptByPubkey(algorithm,
							SM2Pubkey, input.getBytes());
				}
				
				if (!"".equals(input)) {
						if (rs != null) {
							et_encryptrs.setText(Base64.encodeToString(rs,
									Base64.DEFAULT));
						} else {
							et_encryptrs.setText("");
							tv_sub.setText("加密失败" );
						}
					}
			}
		});
		btn_decrypt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				byte[] decrypt;
				String encryptdata = et_encryptrs.getText().toString();
				if (encryptdata.equals("")) {
					tv_sub.setText("解密密文为空，请输入需要解密的密文");
				} else {
					try {
						byte[] en = Base64.decode(encryptdata.getBytes(),
								Base64.DEFAULT);
						
						
						if (101 == algorithm) {
							String RSAPrikey = "MIICXAIBAAKBgQDGUkS4nAOiIewvzGBwc73KJpV1zgVoD+Vh5wDlvhX/q7/1RsuKii2XSAean9skj25zSJ7MA9z2HpF6sESFK8kbEJ3oEblQ4WduCPQuSng4udQoXvjOPfzC5w3X67noFop+CTBgd6czUuWpx0bOeQIjYBh41jTQjzdV4ajYoD6Y8wIDAQABAoGAUzJN+/gPGiQK9h5R0rrrQnOfPZVY9Ue93a1P1hFBH0mItLenSY4gBBfGgNpQz2yQVDKB02pHdsRqGUL667brnP7ZoEfDP/XXkbjja3eevrXS+b6F4WUkE9LtdTT9Eu80Y21m0nuu7ce0nDy9FkTWPBZgaUTkKXVeW8sG4pKHiJECQQDpIkBNcTisbZ1Z7q71pexG+3kRP6+Oy1S/OaJzRep5ul4rj1htdAMUpAu2hormLK+sktVXfBgrB38TvapxSxlbAkEA2cXpvBxLyEJ1O0dYQDHSY4EKPncTDdu6WemkgWrbWmQuHT4bhiZ5V3Cv+jk9B0AUBTKQqhcgPLU+LTcU9G16SQJAb0k+ULlfSE/68NcTBYfyxd9851LzRsKg7bdhaI1TZFAma7y9hWH/pna4cKTA4ScrpMFivaQrFT861f2Pww0KSQJAaPNYka+RVhGxLcBUZ5ubKRknNrNsG2GpjWtsPHKjBA0DrFQiL6SbFejY5l5vau8WtuqHjXFXt+og7Ol+z81zAQJBAILTeWXO+c2eLaZAoXCkd20AEVH5iY0gX+zR+OKiSkxf0WXstevZlOd1KCP3oe3pMWwWDG9ym/ayQPms54yBNqg=";
							decrypt = SoftLibs.getInstance().DecryptByPriKey(algorithm,
									RSAPrikey, en);

						} else if (1011 == algorithm) {
							String RSAPrikey = "MIIEowIBAAKCAQEAqcTudLDTg1pV1tMjGnP8Fm4bHMDzB/0jhvsP6KjbFwj+VdzWemSuDkovTR+7+Xkmc00qD/mpdhUDkPYqxDQt4O1bFaHv376Q5mfYPkSocUkWcIY2mFwOWLv1c1WIqgEdDEUEClsiRNwgKql3kuK+hZQJ+xvMZ372ktCAl2bwN3rDIdc4+CKh7AH2oYXttH6Is5ArwS9bBQnSDY1/WOiANYB76gJWjYZcP/ssq6PxTzgJXa6pderfdbvl2CoZPH5R1WAWvcyRpWu9qFId8TRcvdLH5W9QeRa1z++oBYdgtXx4jtUmU2Zp+Lf6kOnw3x8GUtJRchCaJdel4/0A2sllMQIDAQABAoIBAQCdekeXhs8s8QbfF0tgpmYYiFot+e12Y6Io+4puTvOsEnOaQhfRwMKUDsyBxgYgC/lWyyVRXE2Nc0CznyVciupj0sjC8EwKSEMw9vLoD9gP/bQK/PR3sCy+stuj+Juk1xOS5hIxaQgIyAguirSYVCxWzFPV0ptxBZzD4xTlXF9Hld1MgL8bzJ8wHSwR6d892zyy/rNX7wOc7TIKBG/ry1rFYcZ94kBtT5NNnDnvVtnF6dnxt/xp4aZoJG5vEwdAqe+QzlzKQ0t75Y845P+3o9ZVMoWws+9QGQjZVeSswvqxeZJmM0uY2brNseRk3fNuPmPb2x05PiizRRQ/x++t5+yBAoGBANvaCGOHlo06uA/vw6+Z9m6PqYnlxhvhicT56N/oBhxUVog4bNyJR/4RzxQOwEAuTR/0wdKzKUsgn9syIHIuslgNavmQHQSbki3B/mQ2KUH1/MMnmmvRc0B0EyIBS+mmJxJk72EUmWQZ1ycFwHcuLcfXVQoI/QQDXgfn3qgbao5tAoGBAMWu1gclGkScjULE768HdBSOLJxN4A0yDN+8MZoEH6Rj4+480OB3MHOvgUNQphIZHsr5VxnLFd4AgWIOTEMVikgpuwt9QgnkWZl5SvbBQsRMKVGBHip5cy26D2bX5kTPtiL0rd+bJxdKQgJgRDkPBu5CoJSBYq3hD6J6iM4YG6dVAoGAaR7PdAeYzN6Os9AI2SXc1vCKOA1NtHca0LxNxQHEKjeJSIjyRGC/s5QAV67V7do1tlnpcz2HOQzYYo6NRQte6ws/YyStO07Hjml7oZ7kC037FbUR+Quk9+SIYXINQiLX+8uXSev3jA1u+pGdfV7vNOaZGrtBf3G8vDr6E5dIU+ECgYBiqLNgtX+6pndJz4ffztR8Pci96Y/chhIux3np6AzyunfTOr70iAfCdOEKe4GmnqrjmjWCRf0P4yivi3s8L8/n3Avh3A3Nag3drs9e/20XEvu8Fbi8cgit1aJCLZZa2V+JapwscP69fO4cyXbDE2DeIJqMA1cAeMaYzJbiZN3IUQKBgEDxa3xCbg6USrDXBFyjAWVW8AgjrMINZ8YVqK5uDXq0hhF43xH5VWI1yk7R1WGrJz7H2ThqXwgskNCfA0+HJtTdK8suyyEVpfubj+ey1YlUgpgVLaQteF1QvIqhs5wrnVphGP9UudAgvTvQ0F3ehAYM19qQeR61g9Hh24X4wITj";
							decrypt = SoftLibs.getInstance().DecryptByPriKey(algorithm,
									RSAPrikey, en);

						} else {
							String SM2prikey = "d1qXXDe3U7rjlQ/ngADPyHAreqGx6oLzoV7e/9LF/UE=";
							decrypt = SoftLibs.getInstance().DecryptByPriKey(algorithm,
									SM2prikey, en);
						}
						
						if (decrypt != null) {
							et_decryptrs.setText(new String(decrypt));
						} else {
							tv_sub.setText("解密失败");
						}
					} catch (Exception e) {
						tv_sub.setText("解密失败,密文解base64失败");
					}
				}
			}
		});
	}
}
