package net.icegalaxy;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class XMLReader
{

	private String tradeDate;
	private String filePath;

	double kkResist;
	double kkSupport;
	
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
	Element eElement;

	private Document doc;

	public XMLReader(String tradeDate, String filePath)
	{

		this.tradeDate = tradeDate;
		this.filePath = filePath;
		
		findElementOfToday();
		
	}

	protected void findElementOfToday()
	{
		NodeList nList = null;

		try
		{
			File fXmlFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			nList = doc.getElementsByTagName("OHLC");

			for (int temp = 0; temp < nList.getLength(); temp++)
			{

				Node nNode = nList.item(temp);

				// System.out.println("\nCurrent Element :" +
				// nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{

					Element eElement = (Element) nNode;

					if (tradeDate.equals(eElement.getElementsByTagName("date").item(0).getTextContent().trim()))
					{
						this.eElement = eElement;
						break;
					}

				}
			}
		} catch (Exception e)
		{
			System.out.println(e);
			Global.addLog("Cannot find node of today");
		}

	}

	public String getValueOfNode(String node)
	{
		String value = "";
	
		try
		{
		value = eElement.getElementsByTagName(node).item(0).getTextContent();
		}catch(Exception e)
		{
			e.printStackTrace();
			Global.addLog("Cannot find node: " + node);
		}
		
		return value;
	}
	
	public String getValueOfChildNode(String node, int item)
	{
		String value = "";
	
		try
		{
		value = eElement.getElementsByTagName(node).item(0).getChildNodes().item(item).getTextContent();
		}catch(Exception e)
		{
			e.printStackTrace();
			Global.addLog("Cannot find node: " + node);
		}
		
		return value;
	}
	
	public void findOHLC()
	{

		setpOpen(Double.parseDouble(eElement.getElementsByTagName("pOpen").item(0).getTextContent()));
		setpHigh(Double.parseDouble(eElement.getElementsByTagName("pHigh").item(0).getTextContent()));
		setpLow(Double.parseDouble(eElement.getElementsByTagName("pLow").item(0).getTextContent()));
		setpClose(Double.parseDouble(eElement.getElementsByTagName("pClose").item(0).getTextContent()));
		
		setKkResist(Double.parseDouble(eElement.getElementsByTagName("kkResist").item(0).getTextContent()));
		setKkSupport(Double.parseDouble(eElement.getElementsByTagName("kkSupport").item(0).getTextContent()));
		
		// setpFluc(Double.parseDouble(eElement.getElementsByTagName("pFluc").item(0).getTextContent()));
		// setAOH(Double.parseDouble(eElement.getElementsByTagName("AOH").item(0).getTextContent()));
		// setAOL(Double.parseDouble(eElement.getElementsByTagName("AOL").item(0).getTextContent()));
		// setOpen(Double.parseDouble(eElement.getElementsByTagName("open").item(0).getTextContent()));
		// setnOpen(Double.parseDouble(eElement.getElementsByTagName("nOpen").item(0).getTextContent()));
		// setStop(Boolean.parseBoolean(eElement.getElementsByTagName("stop").item(0).getTextContent().trim()));
		// System.out.println("XMLpHigh : " +
		// eElement.getElementsByTagName("pHigh").item(0).getTextContent());
		// setpEMA5(Double.parseDouble(eElement.getElementsByTagName("pEMA5").item(0).getTextContent()));
		// setpEMA25(Double.parseDouble(eElement.getElementsByTagName("pEMA25").item(0).getTextContent()));
		// setpEMA50(Double.parseDouble(eElement.getElementsByTagName("pEMA50").item(0).getTextContent()));
		// setpEMA100(Double.parseDouble(eElement.getElementsByTagName("pEMA100").item(0).getTextContent()));
		// setpEMA250(Double.parseDouble(eElement.getElementsByTagName("pEMA250").item(0).getTextContent()));
		// setpEMA1200(Double.parseDouble(eElement.getElementsByTagName("pEMA1200").item(0).getTextContent()));

		// Global.addLog("EMA250 csv: " +
		// Double.parseDouble(eElement.getElementsByTagName("pEMA250").item(0).getTextContent()));

	}

	public void updateNode(String nodeName, String content)
	{

		try
		{

			eElement.getElementsByTagName(nodeName).item(0).setTextContent(content);

			// You need to write the content back to the file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filePath));
			transformer.transform(source, result);
			
			System.out.println("Updated value of node:"+ nodeName + " to \"" + content + "\"");
			
		} catch (Exception e)
		{
			e.printStackTrace();
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

	public boolean isStop()
	{
		return stop;
	}

	public void setStop(boolean stop)
	{
		this.stop = stop;
	}

	public double getpOpen()
	{
		return pOpen;
	}

	private void setpOpen(double pOpen)
	{
		this.pOpen = pOpen;
	}

	public double getnOpen()
	{
		return nOpen;
	}

	public void setnOpen(double nOpen)
	{
		this.nOpen = nOpen;
	}

	public double getpHigh()
	{
		return pHigh;
	}

	private void setpHigh(double pHigh)
	{
		this.pHigh = pHigh;
	}

	public double getpLow()
	{
		return pLow;
	}

	private void setpLow(double pLow)
	{
		this.pLow = pLow;
	}

	public double getpClose()
	{
		return pClose;
	}

	private void setpClose(double pClose)
	{
		this.pClose = pClose;
	}

	public double getpFluc()
	{
		return pFluc;
	}

	private void setpFluc(double pFluc)
	{
		this.pFluc = pFluc;
	}

	public double getAOH()
	{
		return AOH;
	}

	private void setAOH(double aOH)
	{
		AOH = aOH;
	}

	public double getAOL()
	{
		return AOL;
	}

	private void setAOL(double aOL)
	{
		AOL = aOL;
	}

	public double getOpen()
	{
		return open;
	}

	private void setOpen(double open)
	{
		this.open = open;
	}

	public double getKkResist()
	{
		return kkResist;
	}

	public void setKkResist(double kkResist)
	{
		this.kkResist = kkResist;
	}

	public double getKkSupport()
	{
		return kkSupport;
	}

	public void setKkSupport(double kkSupport)
	{
		this.kkSupport = kkSupport;
	}
	
	

}
