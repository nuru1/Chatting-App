package com.example.app;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	EditText u,p,l;
	Button s;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        u=(EditText)findViewById(R.id.editText1);
        p=(EditText)findViewById(R.id.editText2);
        l=(EditText)findViewById(R.id.editText3);
        
        
        
        if(!((u.getText().toString()=="")||(p.getText().toString()==""))){
        	task();}
        	else{
        		Toast.makeText(getApplicationContext(), "Enter full details", Toast.LENGTH_LONG).show();
        }
        
        
        
    }
    
    public void task(){
    	String user= u.getText().toString();
        String pass= p.getText().toString();
        String loc= l.getText().toString();
        String res=null; 
        
        SoapObject soap=new SoapObject(NAMESPACE, method);
		soap.addProperty("username",username);
		soap.addProperty("address",address);
		soap.addProperty("userid",userid);
		soap.addProperty("password",password);
		soap.addProperty("location",location);
		
    }
    /*String restex=null;
		SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(soap);
		HttpTransportSE http=new HttpTransportSE(URL);
		try {
			http.call(NAMESPACE+method, envelope);
			SoapPrimitive primitive =(SoapPrimitive) envelope.getResponse();
			restex=primitive.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
			return "error";
			
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return "error";
			
		}
		
		return restex;
	}
    
    
    
    
    */
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
      
    }
    
}






    /*private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.reminders:
                    mTextMessage.setText(R.string.str_reminders);
                    Fragment_Reminders fragment = new Fragment_Reminders();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.Frame,fragment,"Reminders");
                    ft.commit();
                    return true;
                case R.id.add_reminder:
                    mTextMessage.setText(R.string.str_add_reminder);
                    Fragment_Add_Reminder fragment2 = new Fragment_Add_Reminder();
                    FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                    ft2.replace(R.id.Frame,fragment2,"Add Reminder");
                    ft2.commit();
                    return true;
                case R.id.suggestions:
                    mTextMessage.setText(R.string.str_suggestions);
                    Fragment_Suggestions fragment3 = new Fragment_Suggestions();
                    FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                    ft3.replace(R.id.Frame,fragment3,"Suggestions");
                    ft3.commit();
                    return true;
            }
            return false;
        }
    };*/
