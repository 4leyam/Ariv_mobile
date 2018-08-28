package cg.code.aleyam.nzela_nzela.depart;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cg.code.aleyam.nzela_nzela.data_service.Depart_data;
import cg.code.aleyam.nzela_nzela.data_service.Depart_item;
import cg.code.aleyam.nzela_nzela.depart.Depart;
import cg.code.aleyam.nzela_nzela.depart.Depart_fragment;
import cg.code.aleyam.nzela_nzela.depart.InfoGenFragment;

public class Pager_adapter extends FragmentStatePagerAdapter {
    private List<Depart_fragment> onglets = new ArrayList<>();
    private ArrayList<String> titre = null;
    private static boolean isPrice = false;
    private Map <String , ArrayList<String[]>> data = new HashMap<>();
    private final String INFO = "Informations générales";

    Depart_data<Depart_item> all_data;

    /**
     * le constructeur ne prends en paramettre que les titres des onglets et en fonction de ces titre(leur nombre)
     * cree des fragment qu'on remplira
     *
     */

    public Pager_adapter(FragmentManager fm , ArrayList<String> titre , Depart_data<Depart_item> all_data  , boolean isPrice) {
        super(fm);
        this.titre = titre;
        this.isPrice = isPrice;
        this.all_data = all_data;
        if(!all_data.getInfo().isEmpty()) {
            this.titre = getTitles(all_data);
        }
    }

    public ArrayList<String> getTitles(Depart_data<Depart_item> all_data) {
       //chaque niveua de String correspond a un type de donnees et chaques niveau de collection correspond a un depart.
       ArrayList<String> titles = new ArrayList<>();
       //on une map ou les cle corresponde aux titres.
       Map<String , ArrayList<String[]>> contenu = new HashMap<>();
       ArrayList<Depart_item> di = all_data.getDepart();


        if(!isPrice) {
            //les onglets en fonction des titres.
            for (int i = 0 ; i<di.size() ; i++) {
                if(titles.isEmpty()) {
                    titles.add(di.get(i).getDate_depart());
                    ArrayList<String[]> depart = new ArrayList<>();
                    depart.add(dataClassify(di.get(i)));
                    contenu.put(di.get(i).getDate_depart() , depart);
                } else {
                    if (titles.contains(di.get(i).getDate_depart())) {
                        contenu.get(di.get(i).getDate_depart()).add(dataClassify(di.get(i)));

                    } else {
                        //donc si le tritre n'existe pas on ajoute le titre et un contenue de page
                        titles.add(di.get(i).getDate_depart());
                        ArrayList<String[]> depart = new ArrayList<>();
                        depart.add(dataClassify(di.get(i)));
                        contenu.put(di.get(i).getDate_depart() , depart);
                    }
                }
            }
        } else {

            for (int i = 0 ; i<di.size() ; i++) {
                if(titles.isEmpty()) {
                    titles.add(""+di.get(i).getTarif_adult());
                    ArrayList<String[]> depart = new ArrayList<>();
                    depart.add(dataClassify(di.get(i)));
                    contenu.put(""+di.get(i).getTarif_adult() , depart);
                } else {
                    if (titles.contains(""+di.get(i).getTarif_adult())) {
                        contenu.get(""+di.get(i).getTarif_adult()).add(dataClassify(di.get(i)));

                    } else {
                        titles.add(""+di.get(i).getTarif_adult());
                        ArrayList<String[]> depart = new ArrayList<>();
                        depart.add(dataClassify(di.get(i)));
                        contenu.put(""+di.get(i).getTarif_adult() , depart);
                    }
                }

            }
        }
        data = contenu;
        return titles;
    }


    private String[] dataClassify(Depart_item di) {

        String[] depart_info = {
                di.getDate_depart(),
                di.getFormalite(),
                di.getDepart_h(),
                ""+di.getTarif_enfant(),
                ""+di.getPlace_disponible(),
                di.getLieu_amont()+"~"+di.getLieu_aval(),
                di.getImage_bus(),
                ""+di.getId_depart(),
                ""+di.getId_agence() ,
                ""+di.getTarif_adult()
        };
        return depart_info;
    }

    public static boolean isPrice() {
        return isPrice;
    }

    @Override
    public Fragment getItem(int position) {

        //pour donner les informations gle de l'agence.

        if(position == 0) {
            InfoGenFragment igen = new InfoGenFragment();
            if(!all_data.getInfo().isEmpty())
                igen.setInfo(all_data.getInfo().get(0));//donc la on a passe les informations gnerales de l'agence.
            return igen;
        }else {
            Depart_fragment liste_fragment = new Depart_fragment();
            if(!all_data.getInfo().isEmpty()) {
                Log.e("test" , "le titre: "+titre.get(position-1));
                Log.e("test" , data.get(titre.get(position-1)).toString());
                liste_fragment.setElements(data.get(titre.get(position-1)));
            }
            //on fait get Element afin de remplire la liste de toutes les informations adequate. sauf les images.

            return liste_fragment;
        }


    }
    @Override
    public int getCount() {

        return titre.size()+1;
    }



    @Override
    public CharSequence getPageTitle (int position) {

            if (position == 0) return INFO;
            else {
                return titre.get(position-1) ;
            }

    }

    public void setAll_data(Depart_data<Depart_item> all_data) {
        this.all_data = all_data;
        if(!all_data.getInfo().isEmpty()) {
            Log.e("test" , "affichage des departs");
            this.titre = getTitles(all_data);
            for(int i = 0 ; i < data.size() ; i++) {
                for( int j = 0 ; j< data.get(this.titre.get(i)).size() ; j++) {
                    for(int k = 0 ; k< data.get(this.titre.get(i)).get(j).length ; k++) {
                        Log.e("test" , data.get(this.titre.get(i)).get(j)[k]);
                    }
                    Log.e("test" , "Depart suivant");
                }
            }
        }

    }
}