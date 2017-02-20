package net.icegalaxy;

public class TestAPI {

	public static void main(String[] args) {
		String myLibraryPath = System.getProperty("user.dir");//or another absolute or relative path

		System.setProperty("java.library.path", myLibraryPath);
//		 System.loadLibrary("spapidllm64");
		
	//	System.out.println(SPApi.SPApiDll.INSTANCE.SPAPI_Initialize());
//		System.out.println(System.getProperty("sun.arch.data.model"));
		SPApi.init();
		
		try
		{
			Thread.sleep(1000);
		} catch (InterruptedException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		SPApi.subScribePrice();
	
		SPApi.addOrder('B');
		
		try
		{
			Thread.sleep(1000);
		} catch (InterruptedException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
	
		SPApi.addOrder('S');

	}

}
