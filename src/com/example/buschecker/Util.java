package com.example.buschecker;

public abstract class Util {
	
	// Modifique o SERVER_IP de acordo com o IP do seu server
	// 10.0.2.2
	public static final String SERVER_IP = "192.168.1.110";
	public static final String SERVER_URL = "http://" + SERVER_IP + "/buschecker/";
	public static final String GET_DATA_SCRIPT = "getData.php";
	public static final String GET_BUSLINES_SCRIPT = "getBusLines.php";
	public static final String PUT_DATA_SCRIPT = "putData.php";
	
	public static boolean isBlank(String str) {
	    return (str == null) || (str.trim().length() == 0);
	}

}
