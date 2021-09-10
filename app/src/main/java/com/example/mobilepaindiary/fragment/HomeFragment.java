package com.example.mobilepaindiary.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mobilepaindiary.databinding.HomeFragmentBinding;
import com.example.mobilepaindiary.weatherAPI.Response;
import com.example.mobilepaindiary.weatherAPI.RetrofitClient;
import com.example.mobilepaindiary.weatherAPI.RetrofitInterface;
import com.example.mobilepaindiary.weatherAPI.Weather;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeFragment extends Fragment {

    private HomeFragmentBinding homeBinding;
    private RetrofitInterface retrofitInterface;


    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the View for this fragment
        homeBinding = HomeFragmentBinding.inflate(inflater, container, false);
        View view = homeBinding.getRoot();
        // add retrofit
        retrofitInterface = RetrofitClient.getRetrofitService();
        // call weather data from API
        Call<Response> callAsync = retrofitInterface.getWeatherData();
        //makes an async request & invokes callback methods when the response returns
        callAsync.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
            {
                if (response.isSuccessful()) {
                    Weather weather = response.body().getWeather();
                    String tempStr = weather.getTemperature();
                    String pressStr = weather.getPressure();
                    String humidStr = weather.getHumidity();
                    String currentDay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    // convert Kelvin to Celsius
                    double temp = Math.round((Double.valueOf(tempStr)-273.15)* 100.0)/100.0;
                    String temperature = String.valueOf(temp);

                    homeBinding.temperature.setText(temperature+ "°C");
                    homeBinding.pressure.setText("pressure: "+pressStr+ " hPa");
                    homeBinding.humidity.setText("humidity: "+humidStr+"%");
                    homeBinding.date.setText(currentDay);

                    // shared the weather data with other fragments without storing it in the room directly. e.g. data entry fragment
                    SharedPreferences sharedPref= requireActivity().
                            getSharedPreferences("weather", Context.MODE_PRIVATE);
                    SharedPreferences.Editor spEditor = sharedPref.edit();
                    spEditor.putString("temperature", Double.toString(temp));
                    spEditor.putString("pressure", pressStr);
                    spEditor.putString("humidity", humidStr);
                    spEditor.commit();
                }
                else {
                    Log.i("Error ","Response failed");
                }
            }

            // report error if failed
            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(getActivity(),t.getMessage(), Toast.LENGTH_SHORT);
            }

        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        homeBinding = null;
    }
}
