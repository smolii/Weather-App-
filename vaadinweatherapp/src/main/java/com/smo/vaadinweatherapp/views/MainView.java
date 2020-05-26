package com.smo.vaadinweatherapp.views;

import com.smo.vaadinweatherapp.controller.WeatherService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import elemental.json.JsonException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@Route
@PWA(name = "Vaadin Application",
        shortName = "Vaadin App",
        description = "This is an Vaadin Weather Application.",
        enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout {

    @Autowired
    private WeatherService weatherService;
    private VerticalLayout mainLayout;
    private TextField cityTextField;
    private Button searchButton;
    private Select<String> unitSelect;
    private HorizontalLayout dashboard;
    private Label location;
    private Label currentTemp;
    private HorizontalLayout mainDescriptionLayout;
    private Label weatherDescription;
    private Label weatherMin;
    private Label weatherMax;
    private Label pressureLabel;
    private Label humidityLabel;
    private Label windSpeedLabel;
    private Label feelsLike;
    private Image iconImg;
    private HorizontalLayout footer;


    public MainView() {
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        addClassNames("centered-content", "body");
        setFooter();
        iconImg = new Image();
        setLogo();
        setForm();
        dashboardTitle();
        dashboardDetails();


        cityTextField.addKeyPressListener(keyPressEvent -> {
            if (keyPressEvent.getKey().getKeys().toString().equals(Key.ENTER.getKeys().toString())) {
                try {
                    if (!cityTextField.getValue().equals("")) {
                        updateUI();
                    } else {
                        Notification.show("Please Enter The City Name").setPosition(Notification.Position.MIDDLE);
                    }
                } catch (JsonException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        searchButton.addClickListener(buttonClickEvent -> {
            if (!cityTextField.getValue().equals("")) {
                try {
                    updateUI();
                } catch (JsonException | JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Notification.show("Please Enter The City Name").setPosition(Notification.Position.MIDDLE);
            }
        });
    }

    private void setLogo() {
        HorizontalLayout logo = new HorizontalLayout();
        logo.setVerticalComponentAlignment(Alignment.CENTER);
        Image logoImg = new Image("https://www.transparentpng.com/thumb/weather-report/sun-cloud-rain-water-lightning-weather-report-png-0.png", "logo");
        logo.add(logoImg);
        add(logo);
    }

    private void setForm() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setVerticalComponentAlignment(Alignment.CENTER);
        formLayout.setSpacing(true);
        formLayout.setMargin(true);

        // unitSelect
        unitSelect = new Select<>();
        ArrayList<String> items = new ArrayList<>();
        items.add("C");
        items.add("F");
        unitSelect.setItems(items);
        unitSelect.setValue(items.get(0));
        unitSelect.setWidth("70px");
        formLayout.add(unitSelect);

        // cityTextField
        cityTextField = new TextField();
        cityTextField.setWidth("80%");
        formLayout.add(cityTextField);

        // searchButton
        searchButton = new Button("Search");
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        formLayout.add(searchButton);
        add(formLayout);

    }

    private void dashboardTitle() {
        dashboard = new HorizontalLayout();
        dashboard.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        // city location

        location = new Label("Currently in Warsow");
        location.setWidth("200px");


        // current temp

        currentTemp = new Label("10C");

        dashboard.add(location, currentTemp, iconImg);
    }

    private void dashboardDetails() {
        mainDescriptionLayout = new HorizontalLayout();
        mainDescriptionLayout.setAlignItems(Alignment.CENTER);

        //description Layout
        VerticalLayout descriptionLayout = new VerticalLayout();
        descriptionLayout.setAlignItems(Alignment.CENTER);
        descriptionLayout.setHeight("100%");
        descriptionLayout.setSpacing(true);
        descriptionLayout.setPadding(true);
        //Weather Description  dummyData
        weatherDescription = new Label("Description: ");
        weatherDescription.setWidth("250px");
        descriptionLayout.add(weatherDescription);

        //Min weather   dummyData
        weatherMin = new Label();
        descriptionLayout.add(weatherMin);
        //Max weather dummyData
        weatherMax = new Label();
        descriptionLayout.add(weatherMax);

        // Pressure, humidity, wind, Felike

        VerticalLayout pressureLayout = new VerticalLayout();
        pressureLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        pressureLabel = new Label();
        pressureLabel.setWidth("120px");
        pressureLayout.add(pressureLabel);

        humidityLabel = new Label();
        humidityLabel.setWidth("90px");
        pressureLayout.add(humidityLabel);

        windSpeedLabel = new Label();
        windSpeedLabel.setWidth("90px");
        pressureLayout.add(windSpeedLabel);

        feelsLike = new Label();
        feelsLike.setWidth("120px");
        pressureLayout.add(feelsLike);


        mainDescriptionLayout.add(descriptionLayout, pressureLayout);

    }


    private void updateUI() throws JSONException {

        String city = cityTextField.getValue();
        String defaultUnit;
        weatherService.setCityName(city);

        if (unitSelect.getValue().equals("F")) {
            weatherService.setUnit("imperials");
            unitSelect.setValue("F");
            defaultUnit = "\u00b0" + "F";

        } else {
            weatherService.setUnit("metric");
            unitSelect.setValue("C");
            defaultUnit = "\u00b0" + "C";
        }

        location.setText("Currently in " + city);
        JSONObject mainObject = weatherService.returnMain();
        int temp = mainObject.getInt("temp");
        currentTemp.setText(temp + defaultUnit);

        String iconCode = null;
        String weatherDescriptionNew = null;
        JSONArray jsonArray = weatherService.returnWeatherArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject weatherObj = jsonArray.getJSONObject(i);
            iconCode = weatherObj.getString("icon");
            weatherDescriptionNew = weatherObj.getString("description");
        }

        iconImg.setSrc("http://openweathermap.org/img/wn/" + iconCode + "@2x.png");

        weatherDescription.setText("Description: " + weatherDescriptionNew);
        weatherMin.setText("Min Temp: " + weatherService.returnMain().getInt("temp_min") + unitSelect.getValue());
        weatherMax.setText("Max Temp: " + weatherService.returnMain().getInt("temp_max") + unitSelect.getValue());
        pressureLabel.setText("Pressure: " + weatherService.returnMain().getInt("pressure"));
        humidityLabel.setText("Humidity: " + weatherService.returnMain().getInt("humidity"));
        windSpeedLabel.setText("Wind: " + weatherService.returnWind().getInt("speed") + " m/s");
        feelsLike.setText("Feels Like: " + weatherService.returnMain().getInt("feels_like"));

        add(dashboard, mainDescriptionLayout, footer);
    }

    private void setFooter() {
        footer = new HorizontalLayout();
        footer.setAlignItems(Alignment.START);
        footer.setSpacing(true);
        footer.setMargin(true);
        footer.setWidth("100%");
        footer.setHeight("40px");
        Label description = new Label();
        description.setText("Weather App by Sebastian Smoliński.");
        description.setWidth("100%");
        footer.add(description);
    }


}
