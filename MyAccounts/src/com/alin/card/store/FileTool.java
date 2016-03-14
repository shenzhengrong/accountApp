package com.alin.card.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.alin.card.common.CardInfo;
import com.alin.card.encrypt.BASE64;

public class FileTool
{

	public static final int CODE_SUCCSESS = 0;
	
	public static final int CODE_PATH_NOT_EXIST = 1;
	
	public static final int CODE_CREATE_FILE_FAILED = 2;
	
	public static final int CODE_IMPORTFILE_FAILED = 3;
	
	public static final int CODE_EXPORTFILE_FAILED = 4;
	
	public static final int CODE_FILE_ALREADY_EXIST = 5;
	
	public static int importFile(CardInfoOpenHelper openHelper, String path)
	{
		File file = new File(path);
		if(!file.exists())
		{
			return CODE_PATH_NOT_EXIST;
		}
		else
		{
			List<CardInfo> cardInfos = parserCardInfoXML(path);
			if(cardInfos == null)
			{
				return CODE_IMPORTFILE_FAILED;
			}
			for (CardInfo cardInfo : cardInfos) 
			{
				openHelper.onInsert(cardInfo);
			}
		}
		return CODE_SUCCSESS;
	}
	
	public static int exportFile(CardInfoOpenHelper openHelper, String path)
	{
		File file = new File(path);
		if(!file.exists())
		{
			try 
			{
				if(!file.createNewFile())
				{
					return CODE_CREATE_FILE_FAILED;
				}
				if(composeCardInfoXML(openHelper.onQueryAll(), path))
				{
					return CODE_SUCCSESS;
				}
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			return CODE_FILE_ALREADY_EXIST;
		}
		
		return CODE_EXPORTFILE_FAILED;
	}

	public static List<CardInfo> parserCardInfoXML(String filePath)
	{
		if (null == filePath || "".equals(filePath))
		{
			return null;
		}

		List<CardInfo> cardInfos = new ArrayList<CardInfo>();
		CardInfo cardInfo = null;
		FileInputStream fileInputStream = null;
		try
		{
			fileInputStream = new FileInputStream(filePath);

			if(fileInputStream != null)
			{
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				XmlPullParser parser = factory.newPullParser();
				parser.setInput(fileInputStream, "UTF-8");

				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT)
				{
					switch (eventType)
					{
					case XmlPullParser.START_TAG:
					{
						if ("CardInfo".equals(parser.getName()))
						{
							cardInfo = new CardInfo();
						}
						if (null != cardInfo)
						{
							if ("name".equals(parser.getName()))
							{
								cardInfo.setName(BASE64.decodeInfo(parser.nextText()));
							}
							else if ("account".equals(parser.getName()))
							{
								cardInfo.setAccount(BASE64.decodeInfo(parser.nextText()));
							}
							else if ("password".equals(parser.getName()))
							{
								cardInfo.setPassword(BASE64.decodeInfo(parser.nextText()));
							}
							else if ("remark".equals(parser.getName()))
							{
								cardInfo.setRemark(BASE64.decodeInfo(parser.nextText()));
							}
							else
							{
								Log.i("CardInfo", parser.getName());
							}
						}
					}
					break;
					case XmlPullParser.END_TAG:// 结束元素事件  
		                if ("CardInfo".equals(parser.getName()) && cardInfo != null) 
		                {  
		                    cardInfos.add(cardInfo);  
		                    cardInfo = null;  
		                }  
		                break;  
					default:
						break;
					}
					eventType = parser.next();
				}
			}
		}
		catch (XmlPullParserException e)
		{
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (null != fileInputStream)
			{
				try
				{
					fileInputStream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				finally
				{
					fileInputStream = null;
				}
			}
		}

		return cardInfos;
	}

	public static boolean composeCardInfoXML(List<CardInfo> cardInfos,
			String filePath)
	{
		if (null == cardInfos || null == filePath || "".equals(filePath))
		{
			return false;
		}

		StringBuffer xml = new StringBuffer();
		xml.append("<CardInfos>");
		for (int i = 0; i < cardInfos.size(); i++) 
		{
			xml.append("<CardInfo>");
			xml.append("<name>" + BASE64.encodeInfo(cardInfos.get(i).getName()) + "</name>");
			xml.append("<account>" + BASE64.encodeInfo(cardInfos.get(i).getAccount()) + "</account>");
			xml.append("<password>" + BASE64.encodeInfo(cardInfos.get(i).getPassword()) + "</password>");
			xml.append("<remark>" + BASE64.encodeInfo(cardInfos.get(i).getRemark()) + "</remark>");
			xml.append("</CardInfo>");
		}
		xml.append("</CardInfos>");

		FileOutputStream fileOutputStream = null;
		try
		{
			fileOutputStream = new FileOutputStream(filePath);
			{
				fileOutputStream.write(xml.toString().getBytes());
				return true;
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (null != fileOutputStream)
			{
				try
				{
					fileOutputStream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				finally
				{
					fileOutputStream = null;
				}
			}
		}
		return false;
	}
}
