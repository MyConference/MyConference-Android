package es.ucm.myconference.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import es.ucm.myconference.R;
import android.database.Cursor;
import android.database.MatrixCursor;

public final class Data {

	private static long index = 0;
	private static MatrixCursor slideMenu;
	
	public static Cursor getSlideMenuCursor(String[] items){
		if(slideMenu == null){
			slideMenu = new MatrixCursor(new String[] { "_id", "item", "icon" } );
			
			slideMenuRow(items[0], R.drawable.ic_menu_new);
			slideMenuRow(items[1], R.drawable.ic_menu_papers);
			slideMenuRow(items[2], R.drawable.ic_menu_committee);
			slideMenuRow(items[3], R.drawable.ic_menu_speakers);
			slideMenuRow(items[4], R.drawable.ic_menu_venue);
			slideMenuRow(items[5], R.drawable.ic_menu_travel);
			slideMenuRow(items[6], R.drawable.ic_menu_links);
			slideMenuRow(items[7], R.drawable.ic_menu_about);
		}
		
		return slideMenu;
	}
	
	private static void slideMenuRow(String name, int icon){
		slideMenu.addRow(new Object[] {++index, name, icon} );
	}
	
	public static String inputStreamToString(InputStream inputStream){
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        StringBuilder result = new StringBuilder();
        
        try {
			while((line = bufferedReader.readLine()) != null){
			    result.append(line);
			}
	        inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
        return result.toString();
	}
}
