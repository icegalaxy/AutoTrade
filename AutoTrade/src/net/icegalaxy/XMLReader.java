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

	public XMLReader(String tradeDate) {

		this.tradeDate = tradeDate;
		findOHLC();
	}

	private void findOHLC() {

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
					setpFluc(Double.parseDouble(eElement.getElementsByTagName("pFluc").item(0).getTextContent()));
					setAOH(Double.parseDouble(eElement.getElementsByTagName("AOH").item(0).getTextContent()));
					setAOL(Double.parseDouble(eElement.getElementsByTagName("AOL").item(0).getTextContent()));
					setOpen(Double.parseDouble(eElement.getElementsByTagName("open").item(0).getTextContent()));
					setnOpen(Double.parseDouble(eElement.getElementsByTagName("nOpen").item(0).getTextContent()));
				
//					 System.out.println("XMLpHigh : " +
//					 eElement.getElementsByTagName("pHigh").item(0).getTextContent());

				}
			}

		} catch (Exception e) {
			System.out.println(e);
		}
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