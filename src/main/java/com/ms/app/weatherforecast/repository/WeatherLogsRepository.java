package com.ms.app.weatherforecast.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ms.app.weatherforecast.model.WeatherLog;

@Repository
public interface WeatherLogsRepository extends JpaRepository<WeatherLog, Long>{

}
