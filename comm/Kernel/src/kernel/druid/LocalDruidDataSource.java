package kernel.druid;

import com.alibaba.druid.pool.DruidDataSource;

import kernel.util.Endecrypt;

public class LocalDruidDataSource extends DruidDataSource{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8682300581159171345L;
	private String KEY = "Roj6#@08SDF87323FG00%jjsd";
	public void setPassword(String password) {
		Endecrypt endecrypt = new Endecrypt();
        super.setPassword(endecrypt.get3DESDecrypt(password, KEY));
	}
}
