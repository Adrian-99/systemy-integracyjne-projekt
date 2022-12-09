package pl.adrian.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {
    public static void awaitEnterToExit() {
        var input = new Scanner(System.in);
        log.info("Application started, press [ENTER] to exit");
        input.nextLine();
    }
}
