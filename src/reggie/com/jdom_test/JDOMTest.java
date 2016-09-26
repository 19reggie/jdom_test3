package reggie.com.jdom_test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class JDOMTest {

	static String account = "tester";// �˻�
	static String password = "888888";// ����
	static String loginPostUrl = "http://10.250.1.88/zentaopms/www/index.php?m=user&f=login";// ��¼����
	static String bugPostUrl = "http://10.250.1.88/zentaopms/www/index.php?m=bug&f=create&productID=1&branch=0&extra=moduleID=0";// bug������
	static String fileName = System.getProperty("user.dir") + "\\xml\\tmantis_bugs.xml";
	CloseableHttpClient httpclient;

	static JDOMTest jt2 = new JDOMTest();

	public static void main(String[] args) {
		jt2.parserXml(fileName);
	}

	public void submitDefect(String product, String module, String openedBuild, String assignedTo, String title,
			String severity, String steps) {
		// ��¼
		// ���뱣��ͬһ��HttpClient
		httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(loginPostUrl);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("account", account));
		pairs.add(new BasicNameValuePair("password", password));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, Consts.UTF_8);
		httppost.setEntity(entity);
		try {
			CloseableHttpResponse httpresponse = httpclient.execute(httppost);
			httppost.releaseConnection();
			// �ύ
			HttpPost httppost2 = new HttpPost(bugPostUrl);
			List<NameValuePair> pairs2 = new ArrayList<NameValuePair>();
			pairs2.add(new BasicNameValuePair("product", product));
			pairs2.add(new BasicNameValuePair("module", module));
			pairs2.add(new BasicNameValuePair("openedBuild[]", openedBuild));
			pairs2.add(new BasicNameValuePair("assignedTo", assignedTo));
			pairs2.add(new BasicNameValuePair("title", title));
			pairs2.add(new BasicNameValuePair("severity", severity));
			pairs2.add(new BasicNameValuePair("steps", steps));
			UrlEncodedFormEntity entity2 = new UrlEncodedFormEntity(pairs2, Consts.UTF_8);
			httppost2.setEntity(entity2);
			CloseableHttpResponse httpresponse2 = httpclient.execute(httppost2);
			httppost2.releaseConnection();
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void parserXml(String fileName) {
		// ����xml
		SAXBuilder builder = new SAXBuilder();
		try {
			Document document = builder.build(fileName);
			Element mantis = document.getRootElement();
			// System.out.println(mantis.getName());// mantis
			List mantisList = mantis.getChildren("issue");
			System.out.println(mantisList.size());// 4
			for (int i = 0; i < mantisList.size(); i++) {
				Element issue = (Element) mantisList.get(i);
				List issueInfo = issue.getChildren();
				// System.out.println(issueInfo.size());//

				// bug��
				String product = "1";
				String module = "0";// ����ģ��
				String openedBuild = "trunk"; // Ӱ��汾��
				String assignedTo = "tester";// ָ�ɸ�
				String title = ((Element) issueInfo.get(issueInfo.size() - 3)).getValue();// ����
				String severity = "4";// ���صȼ�
				String steps = "<p>[����]" + ((Element) issueInfo.get(issueInfo.size() - 2)).getValue() + "</p>" + "\n"
						+ "<p>[���]" + ((Element) issueInfo.get(issueInfo.size() - 1)).getValue() + "</p>" + "\n"
						+ "<p>[����]</p>";
				System.out.println("��ʼ�ύ��" + (i + 1) + "��Bug.");

				System.out.println("Bug����" + "\n" + ((Element) issueInfo.get(0)).getValue() + "\n"
						+ ((Element) issueInfo.get(issueInfo.size() - 3)).getValue() + "\n"
						+ ((Element) issueInfo.get(issueInfo.size() - 2)).getValue() + "\n"
						+ ((Element) issueInfo.get(issueInfo.size() - 1)).getValue());

				// �ᵥ
				submitDefect(product, module, openedBuild, assignedTo, title, severity, steps);
				System.out.println("��" + (i + 1) + "��Bug�ύ���.");
				System.out.println("------------------");
			}

		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}

		try {
			httpclient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
