package cs464;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import com.sun.tools.javac.code.Attribute.Array;

import cs464.config.AppConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class TwitterApiDemo {

	public static void main(String[] args) throws IOException {
		final String userName = "metanet"; // it's me.

		final AppConfig config = new AppConfig();
		final CloseableHttpClient httpClient = HttpClients.createDefault();
		final ObjectMapper objectMapper = new ObjectMapper();

		final TwitterApi twitter = new TwitterApi(config, httpClient,
				objectMapper);

		BufferedReader br = null;

		TwitterFactory tf = new TwitterFactory();
		Twitter tw = tf.getInstance();
		
        final List<String> allScreenNames = Files.readLines(new File("05"), Charset.defaultCharset());
//		System.out.println(allScreenNames.toString());
		twitter.getUserTimeLineByName(tw, allScreenNames, "05_user_tweets");
		
		// try {
		// System.out.println(tw.getHomeTimeline());
		// } catch (TwitterException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		
		/*
		 * Read data as object
		 */
//		ArrayList<String> list_of_user = new ArrayList<String>();
//		try {
//			list_of_user = readObject("users_list.data");
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		

		/*
		 * Get all user tweets, limitation problem
		 */
//		for(int i = 0; i < list_of_user.size() - 100; i = i + 100) {
//			HashMap<String, List<Status>> map = twitter.getUserTimeLineByName(
//					tw, list_of_user.subList(i, i+100));
//			writeObject("tweets_map_" + i + ".data", map);
//		}

		// final Collection<String> friends =
		// twitter.friendsScreenNamesByScreenName(userName);

		// System.out.println("FriendsSize: " + friends.size());
		// System.out.println("Friends: " + friends);

		// final Collection<String> followers = twitter
		// .followerScreenNamesByScreenName(userName);

		// System.out.println("FollowersSize: " + followers.size());
		// System.out.println("Followers: " + followers);
		//
		// System.out.println("BearerToken: " + twitter.getBearerToken());
		//
		// System.out.println("Followers of 1121871337: " +
		// twitter.followerIdsByUserId("1121871337"));
		
		

		httpClient.close();

	}
	
	private static void writeToFile(String fileName, String line) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
            out.println(line);
        } catch (IOException e) {
            System.out.println("Line not written: " + line);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

	public static ArrayList<String> readObject(String fileName)
			throws IOException, ClassNotFoundException {

		FileInputStream fin = new FileInputStream(fileName);
		ObjectInputStream ois = new ObjectInputStream(fin);
		return (ArrayList<String>) ois.readObject();
	}

	public static void writeObject(String fileName, Object j)
			throws IOException {
		FileOutputStream fout = new FileOutputStream(fileName);
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		oos.writeObject(j);
	}

	public static void writeString(String fileName, String data) {

		FileOutputStream fop = null;
		File file;

		try {

			file = new File(fileName);
			fop = new FileOutputStream(file);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = data.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void getAllUsers(TwitterApi tw, ArrayList<String> list,
			int requestCount, int index) {

		if (list.size() > 20000)
			return;

		if (requestCount == 15) {

			while (requestCount > 1) {

				try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				requestCount--;
			}
		}

		Collection<String> tempList = tw.followerScreenNamesByScreenName(list
				.get(index));
		if (tempList.size() < 1) {
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			list.addAll(tempList);
		}
		requestCount++;
		index++;
		getAllUsers(tw, list, requestCount, index);
	}

}
