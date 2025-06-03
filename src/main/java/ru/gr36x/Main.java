package ru.gr36x;

import ru.gr36x.service.LibraryService;
import ru.gr36x.ui.LibraryWindow;

public class Main {
    public static void main(String[] args) {
        LibraryService service = new LibraryService();
        LibraryWindow.launch(service);
    }
}