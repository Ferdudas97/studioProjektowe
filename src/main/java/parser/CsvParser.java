package parser;

import lombok.experimental.UtilityClass;
import model.Profile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CsvParser {

    private final String DELIMiTER = ";";
    private final int DAYS_COLUMN = 1;
    private final int PEOPLE_COUNT_COLUMN = 2;
    private final int PUBLIC_TRANSPORT_COLUMN = 3;
    private final int CAR_COLUMN = 4;
    private final int RESTAURANT_COLUMN = 5;

    public List<Profile> parseFile(final File csvFile) {

        try {
            return dropHeader(Files.readAllLines(csvFile.toPath())).stream()
                    .map(line -> line.split(DELIMiTER))
                    .map(CsvParser::parseRow)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private Profile parseRow(final String[] row) {
        return Profile.builder()
//                .daysToSpend(parseInt(row[DAYS_COLUMN]))
//                .peopleCount(parseInt(row[PEOPLE_COUNT_COLUMN]))
//                .publicTransport(PreferenceWeight.of(parseDouble(row[PUBLIC_TRANSPORT_COLUMN]), "public_transport"))
//                .usingCar(PreferenceWeight.of(parseDouble(row[CAR_COLUMN]), "car_transport"))
//                .eatingInRestaurants(PreferenceWeight.of(parseDouble(row[RESTAURANT_COLUMN]), "eating in restaurant"))
                .build();
    }

    private List<String> dropHeader(final List<String> rows) {
        return rows.subList(1, rows.size() - 1);
    }
}
