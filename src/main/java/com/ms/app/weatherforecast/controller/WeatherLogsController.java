package com.ms.app.weatherforecast.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ms.app.weatherforecast.dto.WeatherLogDTO;
import com.ms.app.weatherforecast.exception.ResourceNotFoundException;
import com.ms.app.weatherforecast.model.WeatherLog;
import com.ms.app.weatherforecast.repository.WeatherLogsRepository;
import com.ms.app.weatherforecast.utils.Constants;

@RestController
@RequestMapping("/weather/api")
public class WeatherLogsController {

	@Autowired
	WeatherLogsRepository weatherLogsRepository;

	// Get all weather log history
	@GetMapping("/fetchAll")
	public List<WeatherLog> getWeatherLogsHistory() {
		return weatherLogsRepository.findAll();
	}

	// Delete a weather log
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteWeatherLog(@PathVariable(value = "id") Long weatherLogId) {
		WeatherLog weatherLog = weatherLogsRepository.findById(weatherLogId)
				.orElseThrow(() -> new ResourceNotFoundException("WeatherLog", "id", weatherLogId));

		weatherLogsRepository.delete(weatherLog);
		return ResponseEntity.ok().build();
	}

	// Get a weather details for a specific city
	@GetMapping("/forecast/{city}")
	public ResponseEntity<?> getForecast(@PathVariable(value = "city") String cityName) {

		String url = Constants.API_ENDPOINT + "?q=" + cityName + "&APPID=" + Constants.API_KEY;
		RestTemplate restTemplate = new RestTemplate();
		//JSONObject result = restTemplate.getForObject(url, JSONObject.class);
		
		String resultString = restTemplate.getForObject(url, String.class);
		JSONObject result;

		WeatherLogDTO weatherLogDTO = new WeatherLogDTO();
		WeatherLog weatherLog = new WeatherLog();

		weatherLogDTO.setCityName(cityName);
		DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
		Date date = new Date();
		weatherLogDTO.setSearchDate(dateFormat.format(date));

		try {
			result = new JSONObject(resultString);
			weatherLogDTO.setHumidity(result.getJSONObject("main").getInt("humidity") + "%");
			weatherLogDTO.setPressure(result.getJSONObject("main").getInt("pressure") + " hpa");
			weatherLogDTO.setTemperature(result.getJSONObject("main").getDouble("temp") + " Celcius");
			weatherLogDTO.setWindSpeed(result.getJSONObject("wind").getDouble("speed") + " m/s");
			weatherLogDTO.setWeatherCondition(result.getJSONArray("weather").getJSONObject(0).getString("main"));
			
			weatherLog.setCityName(cityName);
			weatherLog.setHumidity(weatherLogDTO.getHumidity());
			weatherLog.setPressure(weatherLogDTO.getPressure());
			weatherLog.setTemperature(weatherLogDTO.getTemperature());
			weatherLog.setWindSpeed(weatherLogDTO.getWindSpeed());
			weatherLog.setWeatherCondition(weatherLogDTO.getWeatherCondition());
			weatherLog.setSearchDate(new Date());
			
			weatherLogsRepository.save(weatherLog);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(Constants.NOT_FOUND, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<WeatherLogDTO>(weatherLogDTO, HttpStatus.OK);
		
	}

}
