package net.icegalaxy;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class XMLReader {

	private String tradeDate;

	double pOpen;
	double pHigh;
	double pLow;
	double pClose;
	double pFluc;
	
	double AOH;
	double AOL;
	double open;
	double nOpen;
	
	double pEMA5;
	double pEMA25;
	double pEMA50;
	double pEMA100;
	double pEMA250;
	double pEMA1200;
	
	boolean stop;

	public XMLReader(String tradeDate) {

		this.tradeDate = tradeDate;
		findOHLC();
	}

	public void findOHLC() {

		NodeList nList = null;

		try {
			File fXmlFile = new File("Z:\\TradeData\\FHIdata.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			System.out.println("XML 1");
			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			nList = doc.getElementsByTagName("OHLC");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				System.out.println("\nCurrent Element :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					if (!tradeDate.equals(eElement.getElementsByTagName("date").item(0).getTextContent().trim()))
						continue;

					setpOpen(Double.parseDouble(eElement.getElementsByTagName("pOpen").item(0).getTextContent()));
					setpHigh(Double.parseDouble(eElement.getElementsByTagName("pHigh").item(0).getTextContent()));
					setpLow(Double.parseDouble(eElement.getElementsByTagName("pLow").item(0).getTextContent()));
					setpClose(Double.parseDouble(eElement.getElementsByTagName("pClose").item(0).getTextContent()));
//					setpFluc(Double.parseDouble(eElement.getElementsByTagName("pFluc").item(0).getTextContent()));
//					setAOH(Double.parseDouble(eElement.getElementsByTagName("AOH").item(0).getTextContent()));
//					setAOL(Double.parseDouble(eElement.getElementsByTagName("AOL").item(0).getTextContent()));
//					setOpen(Double.parseDouble(eElement.getElementsByTagName("open").item(0).getTextContent()));
//					setnOpen(Double.parseDouble(eElement.getElementsByTagName("nOpen").item(0).getTextContent()));
//					setStop(Boolean.parseBoolean(eElement.getElementsByTagName("stop").item(0).getTextContent().trim()));
//					 System.out.println("XMLpHigh : " +
//					 eElement.getElementsByTagName("pHigh").item(0).getTextContent());
//					setpEMA5(Double.parseDouble(eElement.getElementsByTagName("pEMA5").item(0).getTextContent()));
//					setpEMA25(Double.parseDouble(eElement.getElementsByTagName("pEMA25").item(0).getTextContent()));
//					setpEMA50(Double.parseDouble(eElement.getElementsByTagName("pEMA50").item(0).getTextContent()));
//					setpEMA100(Double.parseDouble(eElement.getElementsByTagName("pEMA100").item(0).getTextContent()));
//					setpEMA250(Double.parseDouble(eElement.getElementsByTagName("pEMA250").item(0).getTextContent()));
//					setpEMA1200(Double.parseDouble(eElement.getElementsByTagName("pEMA1200").item(0).getTextContent()));
					
//					Global.addLog("EMA250 csv: " + Double.parseDouble(eElement.getElementsByTagName("pEMA250").item(0).getTextContent()));
				}
			}

		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	
	
	public double getpEMA5()
	{
		return pEMA5;
	}

	public void setpEMA5(double pEMA5)
	{
		this.pEMA5 = pEMA5;
	}

	public double getpEMA25()
	{
		return pEMA25;
	}

	public void setpEMA25(double pEMA25)
	{
		this.pEMA25 = pEMA25;
	}

	public double getpEMA50()
	{
		return pEMA50;
	}

	public void setpEMA50(double pEMA50)
	{
		this.pEMA50 = pEMA50;
	}

	public double getpEMA100()
	{
		return pEMA100;
	}

	public void setpEMA100(double pEMA100)
	{
		this.pEMA100 = pEMA100;
	}

	public double getpEMA250()
	{
		return pEMA250;
	}

	public void setpEMA250(double pEMA250)
	{
		this.pEMA250 = pEMA250;
	}

	public double getpEMA1200()
	{
		return pEMA1200;
	}

	public void setpEMA1200(double pEMA1200)
	{
		this.pEMA1200 = pEMA1200;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public double getpOpen() {
		return pOpen;
	}

	private void setpOpen(double pOpen) {
		this.pOpen = pOpen;
	}
	
	

	public double getnOpen() {
		return nOpen;
	}

	public void setnOpen(double nOpen) {
		this.nOpen = nOpen;
	}

	public double getpHigh() {
		return pHigh;
	}

	private void setpHigh(double pHigh) {
		this.pHigh = pHigh;
	}

	public double getpLow() {
		return pLow;
	}

	private void setpLow(double pLow) {
		this.pLow = pLow;
	}

	public double getpClose() {
		return pClose;
	}

	private void setpClose(double pClose) {
		this.pClose = pClose;
	}

	public double getpFluc() {
		return pFluc;
	}

	private void setpFluc(double pFluc) {
		this.pFluc = pFluc;
	}

	public double getAOH() {
		return AOH;
	}

	private void setAOH(double aOH) {
		AOH = aOH;
	}

	public double getAOL() {
		return AOL;
	}

	private void setAOL(double aOL) {
		AOL = aOL;
	}

	public double getOpen() {
		return open;
	}

	private void setOpen(double open) {
		this.open = open;
	}

	
	
}
