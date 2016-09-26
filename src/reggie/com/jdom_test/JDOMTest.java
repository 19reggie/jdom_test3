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

	static String account = "tester";// 账户
	static String password = "888888";// 密码
	static String loginPostUrl = "http://10.250.1.88/zentaopms/www/index.php?m=user&f=login";// 登录请求
	static String bugPostUrl = "http://10.250.1.88/zentaopms/www/index.php?m=bug&f=create&productID=1&branch=0&extra=moduleID=0";// bug表单请求
	static String fileName = System.getProperty("user.dir") + "\\xml\\tmantis_bugs.xml";
	CloseableHttpClient httpclient;

	static JDOMTest jt2 = new JDOMTest();

	public static void main(String[] args) {
		jt2.parserXml(fileName);
	}

	public void submitDefect(String product, String module, String openedBuild, String assignedTo, String title,
			String severity, String steps) {
		// 登录
		// 必须保持同一个HttpClient
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
			// 提交
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
		// 解析xml
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

				// bug表单
				String product = "1";
				String module = "0";// 所属模块
				String openedBuild = "trunk"; // 影响版本号
				String assignedTo = "tester";// 指派给
				String title = ((Element) issueInfo.get(issueInfo.size() - 3)).getValue();// 标题
				String severity = "4";// 严重等级
				String steps = "<p>[步骤]" + ((Element) issueInfo.get(issueInfo.size() - 2)).getValue() + "</p>" + "\n"
						+ "<p>[结果]" + ((Element) issueInfo.get(issueInfo.size() - 1)).getValue() + "</p>" + "\n"
						+ "<p>[期望]</p>";
				System.out.println("开始提交第" + (i + 1) + "个Bug.");

				System.out.println("Bug描述" + "\n" + ((Element) issueInfo.get(0)).getValue() + "\n"
						+ ((Element) issueInfo.get(issueInfo.size() - 3)).getValue() + "\n"
						+ ((Element) issueInfo.get(issueInfo.size() - 2)).getValue() + "\n"
						+ ((Element) issueInfo.get(issueInfo.size() - 1)).getValue());

				// 提单
				submitDefect(product, module, openedBuild, assignedTo, title, severity, steps);
				System.out.println("第" + (i + 1) + "个Bug提交完成.");
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
