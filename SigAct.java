package com.sklois.demo.test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.ccit.security.sdk.clientDemo.R;
import com.sklois.haiyunKms.SoftLibs;
import com.sklois.demo.util.Base64;

public class SigAct extends FinalActivity {

	@ViewInject(id = R.id.rg_asy)
	RadioGroup rg_asy;

	@ViewInject(id = R.id.radio_sm2_asy)
	RadioButton radio_sm2;

	@ViewInject(id = R.id.radio_rsa_asy)
	RadioButton radio_rsa_asy;
	
	@ViewInject(id = R.id.radio_rsa_sha256_asy)
	RadioButton radio_rsa_sha256_asy;
	
	@ViewInject(id = R.id.radio_rsa_asy1)
	RadioButton radio_rsa2048_asy;
	
	@ViewInject(id = R.id.radio_rsa_sha256_asy1)
	RadioButton radio_rsa2048_sha256_asy;
	
	@ViewInject(id = R.id.radio_sm2_asy)
	RadioButton radio_sm2_asy;

	@ViewInject(id = R.id.et_src)
	EditText et_src;

	@ViewInject(id = R.id.et_sigrs)
	EditText et_sigrs;

	@ViewInject(id = R.id.et_verifyrs)
	EditText et_verifyrs;

	@ViewInject(id = R.id.btn_sig)
	Button btn_sig;

	@ViewInject(id = R.id.btn_verify)
	Button btn_verify;

	@ViewInject(id = R.id.tv_sub)
	TextView tv_sub;

	// 算法
	int algorithm = 0x00001004;
	String sig_b="";
	String src_b="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sig);
		rg_asy.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			/*
			 * 
			 * //签名算法标识 #define SGD_SHA1_RSA 0x00001002 #define SGD_SHA256_RSA
			 * 0x00001004 #define SGD_SM3_SM2 0x00020101
			 */
			
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == radio_rsa_asy.getId()) {
					algorithm = 0x00001002;
				} else if (checkedId == radio_rsa_sha256_asy.getId()) {
					algorithm = 0x00001004;
				} else if (checkedId == radio_rsa2048_asy.getId()) {
					algorithm = 0x00001006;
				} else if (checkedId == radio_rsa2048_sha256_asy.getId()) {
					algorithm = 0x00001008;
				}else {
					algorithm = 0x00020101;
				}
			}
		});
		btn_sig.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String src = et_src.getText().toString();
				if (src.equals("")) {
					tv_sub.setText("请输入签名原文");
				} else {					
					String RSAPrikey = "MIICXAIBAAKBgQDGUkS4nAOiIewvzGBwc73KJpV1zgVoD+Vh5wDlvhX/q7/1RsuKii2XSAean9skj25zSJ7MA9z2HpF6sESFK8kbEJ3oEblQ4WduCPQuSng4udQoXvjOPfzC5w3X67noFop+CTBgd6czUuWpx0bOeQIjYBh41jTQjzdV4ajYoD6Y8wIDAQABAoGAUzJN+/gPGiQK9h5R0rrrQnOfPZVY9Ue93a1P1hFBH0mItLenSY4gBBfGgNpQz2yQVDKB02pHdsRqGUL667brnP7ZoEfDP/XXkbjja3eevrXS+b6F4WUkE9LtdTT9Eu80Y21m0nuu7ce0nDy9FkTWPBZgaUTkKXVeW8sG4pKHiJECQQDpIkBNcTisbZ1Z7q71pexG+3kRP6+Oy1S/OaJzRep5ul4rj1htdAMUpAu2hormLK+sktVXfBgrB38TvapxSxlbAkEA2cXpvBxLyEJ1O0dYQDHSY4EKPncTDdu6WemkgWrbWmQuHT4bhiZ5V3Cv+jk9B0AUBTKQqhcgPLU+LTcU9G16SQJAb0k+ULlfSE/68NcTBYfyxd9851LzRsKg7bdhaI1TZFAma7y9hWH/pna4cKTA4ScrpMFivaQrFT861f2Pww0KSQJAaPNYka+RVhGxLcBUZ5ubKRknNrNsG2GpjWtsPHKjBA0DrFQiL6SbFejY5l5vau8WtuqHjXFXt+og7Ol+z81zAQJBAILTeWXO+c2eLaZAoXCkd20AEVH5iY0gX+zR+OKiSkxf0WXstevZlOd1KCP3oe3pMWwWDG9ym/ayQPms54yBNqg=";
					String SM2prikey = "d1qXXDe3U7rjlQ/ngADPyHAreqGx6oLzoV7e/9LF/UE=";
					String RSAPrikey2048 = "MIIEogIBAAKCAQEA3jofUzmTfqdTCAYsgoThTzDeJ4V5/pGWSwvCyblL5X/qZ01zPCfcobMUeLBuog9/SpwrK/e6eGHA3gDKPfJbv54oZLX01sFMGK3hcY0i+ieWZ9Fi1l3Ks355rCiC6Ld715Xqq652OIdNhzeJl15sfcAVuOuTseMFnV3qlTndwu+8UT9gPYhB/X/Y7aQwkHWfYPVvRh+pYGVO4sugKmmQ2wSj4Mx2gP4v/bC6BL1ZO7DIy4XHFRmksE3izqMjZBlr9RfnieSWfu23x3Jkp4+EFEpnoBwF9uBgtteGw9c9giFuZOPTzAgHSISzxeFxyxxweOzFcm3k+sCJhOY7wkGvbwIDAQABAoIBAHpXpUc8O2dO1vdj7r0sSp4+WQ+I/FCZ9hP6tZI44P/IHFQi334Os/iRjoOjHkU6IuoAcAQUriP4LSYLaUwRUVF80OAhDFnusOYlVCgfe1Ic2UDulWtO13lZ98I/y1IzMYrOjhNWSN92HciDWsxqv2+7gDn996HHSiAIxEzDPsNCEcQu5/5o69itt+Q1yBJonWwix52kiwEgqPri2LZwqE5Re7xwd6xgksN8L2eu9QjBfme5Xpazy6rncAF2rn06HMAnuAqHPbhCOfIymjVaCjs9hXQyNTgCyEyKZPL5uHC6blk/I3Vop7UnmiDmse82ipQp9qDBhr4KZ9Q9DEnDyAECgYEA9NJpYlnGDcuNL4XgwpauU63PrP0VFQEXb3Rv7G8X8lNwokUwDxQMCGAxrnUzuzqx5CHYgsFLWor8d+f2W4k+wLBnZCdVIKXOqPRbC/lIjzh/lU5GTgJKzqCSH++EnWe9NGWkWcxSDJFTzIugqorlFyY2Mpb8QPNqR2SUYzJIcEECgYEA6F+ckzHbj5Jgb9WPHudh6ytem8wTtrrzNSyoFEapYiQsg2HaeShueKTE9NQBS97HNwRUGsbKTKcOPyFvBez1DLbAPDBGEHkxUeMCXqQkrLt++0pJrMHlJlspdr3aqVaKJo7gI3XOETWMwB1MVnplB7xfiVvIjE4OIMJmRtxgM68CgYA6pUivUZf65/67GpPbqSK7UtCDvOBW9rr5bOf+uCIBX3GEvZQkmIXFX92mz9aDjCze3Ci0Mb9uQgr2Yi+Gf9rzZ45s1Gs09eMKX/aLaLyz6jHNv0FiIHRxCM/YLJUU66vMKGj1RdeFwS5UukHTeJAmOnYTKk3k9SpJTdOp63qkwQKBgC7mFSjUGDcq2NHZbVBUbLvU/fw9vQDVTH6/65BkDo2jNWSy3DA1y84Hm0S6gSR56wUcl4jr9CpyUPrG2ax9fZu6SqQbLhjnDOA1C+ZkBI7kLKW1AZefbotGPOEdQkOJFsrfbTda8GMixqyDfUO64ickirejUZ+XpVxRP+kvUy87AoGAcz1vXcCy5g2NlfxRQQGMSsBnEfiePktPHf972s4AmxOjBRIufX2j14IePFS+CluqZGMG5dHUP5yk90W89uHcaUIdxPKfNSOknw/7WYyWgXBlrK9peliSJxGEqs6bQb00M6gy8bzhbtdHbZQVnxtZ938W286b0lZR21wBFo94wig=";
					
					byte[] rs ;
					if (0x00001002 == algorithm) {						
						rs = SoftLibs.getInstance().SignByPriKey(algorithm,
								RSAPrikey, src.getBytes());							
					} else if (0x00001004 == algorithm) {
						rs = SoftLibs.getInstance().SignByPriKey(algorithm,
								RSAPrikey, src.getBytes());	
					}else if (0x00001006 == algorithm) {
						rs = SoftLibs.getInstance().SignByPriKey(algorithm,
								RSAPrikey2048, src.getBytes());	
					}else if (0x00001008 == algorithm) {
						rs = SoftLibs.getInstance().SignByPriKey(algorithm,
								RSAPrikey2048, src.getBytes());	
					}else {
						rs = SoftLibs.getInstance().SignByPriKey(algorithm,
								SM2prikey, src.getBytes());	
					}
					
					if (rs != null) {
						sig_b = Base64.encodeToString(rs,
								Base64.DEFAULT).trim();
						et_sigrs.setText(sig_b);
						src_b=src;
					} else {
						tv_sub.setText("签名失败" );
					}
				}
			}
		});
		btn_verify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String src = et_src.getText().toString();
				String sig = et_sigrs.getText().toString();
				if (src.equals("") || sig.equals("")) {
					tv_sub.setText("请输入签名原文和签名值");
				} else {
					
				
					
					/*
					 * 
						if (src.equals(src_b)&&sig_b.equals(sig.trim())) {
							et_verifyrs.setText("验签成功");
						}else{
							et_verifyrs.setText("验签失败");
						} 
					* */
					
					String RSAPubkey = "MIGJAoGBAMZSRLicA6Ih7C/MYHBzvcomlXXOBWgP5WHnAOW+Ff+rv/VGy4qKLZdIB5qf2ySPbnNInswD3PYekXqwRIUryRsQnegRuVDhZ24I9C5KeDi51Che+M49/MLnDdfruegWin4JMGB3pzNS5anHRs55AiNgGHjWNNCPN1XhqNigPpjzAgMBAAE=";
					String SM2Pubkey = "Cwa48zT3XS9l2yd0ecJcqUKVmWtJinWh0ZLIwrdhDBCsmRjFfGANhPRYLvY4kUYqowa6sfseNUKgt7hbklcxMw==";
					String RSAPubkey2048 = "MIIBCgKCAQEA3jofUzmTfqdTCAYsgoThTzDeJ4V5/pGWSwvCyblL5X/qZ01zPCfcobMUeLBuog9/SpwrK/e6eGHA3gDKPfJbv54oZLX01sFMGK3hcY0i+ieWZ9Fi1l3Ks355rCiC6Ld715Xqq652OIdNhzeJl15sfcAVuOuTseMFnV3qlTndwu+8UT9gPYhB/X/Y7aQwkHWfYPVvRh+pYGVO4sugKmmQ2wSj4Mx2gP4v/bC6BL1ZO7DIy4XHFRmksE3izqMjZBlr9RfnieSWfu23x3Jkp4+EFEpnoBwF9uBgtteGw9c9giFuZOPTzAgHSISzxeFxyxxweOzFcm3k+sCJhOY7wkGvbwIDAQAB";
										
					boolean bRet;
					byte[] bytesig = null;
					
					try {
						bytesig =  Base64.decode(sig.getBytes(),
								Base64.DEFAULT);
			        } catch (Exception e) {
			           et_verifyrs.setText("base64解码失败");
			        }
					
					
					if (0x00001002 == algorithm) {						
						bRet = SoftLibs.getInstance().VerifyByPubkey(algorithm,
								RSAPubkey, src.getBytes(), bytesig);						
					} else if (0x00001004 == algorithm) {
						bRet = SoftLibs.getInstance().VerifyByPubkey(algorithm,
								RSAPubkey, src.getBytes(), bytesig);
					}else if (0x00001006 == algorithm) {
						bRet = SoftLibs.getInstance().VerifyByPubkey(algorithm,
								RSAPubkey2048, src.getBytes(), bytesig);
					}else if (0x00001008 == algorithm) {
						bRet = SoftLibs.getInstance().VerifyByPubkey(algorithm,
								RSAPubkey2048, src.getBytes(), bytesig);
					}else {
						bRet = SoftLibs.getInstance().VerifyByPubkey(algorithm,
								SM2Pubkey, src.getBytes(), bytesig);
					}
					if (bRet != true) {
								et_verifyrs.setText("验签失败");
							} else {
								et_verifyrs.setText("验签成功");
						}
					
					}
				}
			
		});
	}
}
