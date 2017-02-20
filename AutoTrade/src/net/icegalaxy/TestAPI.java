package net.icegalaxy;

public class TestAPI {

	public static void main(String[] args) {
		String myLibraryPath = System.getProperty("user.dir");//or another absolute or relative path

		System.setProperty("java.library.path", myLibraryPath);
//		 System.loadLibrary("spapidllm64");
		
		System.out.println(SPApi.SPApiDll.INSTANCE.SPAPI_Initialize());

	}

}
