package fr.kenda.winzoriamenu.managers;

import fr.kenda.winzoriamenu.WinzoriaMenu;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PluginVersionning{

    private static final String API_URL = "https://api.github.com/repos/KendaFR/WinzoriaMenu/releases/latest";
    private final String lastestName;

    public PluginVersionning() throws IOException {
        String jsonString = getJsonString();

        // Créez un JSONObject à partir de la chaîne JSON
        JSONObject obj = new JSONObject(jsonString);

        // Créez un JSONArray à partir de la chaîne JSON
        JSONArray assets = obj.getJSONArray("assets");

    // Obtenez le premier objet du tableau
        JSONObject firstAsset = assets.getJSONObject(0);

    // Maintenant, vous pouvez obtenir le nom
        lastestName = firstAsset.getString("name");

    }

    private static String getJsonString() throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

        // Lisez une seule ligne de la réponse
        return br.readLine();
    }

    public boolean isLatestVersion()
    {
        String currentName = WinzoriaMenu.getInstance().getDescription().getName() + "-" + WinzoriaMenu.getInstance().getDescription().getVersion() + ".jar";
        return lastestName.equalsIgnoreCase(currentName);
    }

}
