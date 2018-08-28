package cg.code.aleyam.nzela_nzela.data_service;

import cg.code.aleyam.nzela_nzela.transaction.paiement.PaiementObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Nzela_service {

    String API_GET_TOKEN = "web/app/Rest/braintree/main.php";
    String API_CHECK_OUT = "web/app/Rest/braintree/checkout.php";
    String RQUEST_FILTER = "web/app/Rest/DepartFilter.php";

    String WEBSITE = "http://nzela-nzela.000webhostapp.com/";
    //la racine principale du site web
    String TOKEN = "6b41f839ee89d27f532540454268580b68a9828e";
    String[] FILTER_LIST = new String[]{"new" , "popular" , "old" , "l_exp" };
    String NEW_FILTER = FILTER_LIST[0] , POPULAR_FILTER = FILTER_LIST[1] , OLD_FILTER = FILTER_LIST[2] , L_EXP_FILTER = FILTER_LIST[3] ;


    //https://nzela-nzela.000webhostapp.com/second/app/depart.data.php?id=142&token=6b41f839ee89d27f532540454268580b68a9828e

    @GET("second/Rest/agence_data.php")
    Call<Agence_data> getAgence_data(@Query(value = "filtre", encoded = true) String filtre_value, @Query(value = "token", encoded = true) String token);


    /**
     * fonction permettant de faire verifier la correspondance des tuples de la base de donne aux caractere entre dans la barre.
     * @param needle
     * @param token
     * @return
     */
    @POST("second/Rest/ag_search_data.php")
    Call<Agence_data> searchAgence(@Body String needle, @Query(value = "token", encoded = true) String token);

    /**
     * getOneAgenceInfo
     * cette methode permet de recuperer les information d'un depart specifique
     * @param id
     * identifiant de l agence en question
     * @param token
     * jeton de connection.
     * @return
     */
    @GET("second/Rest/agence_data.php")
    Call<Agence_data> getOneAgenceInfo(@Query(value = "id", encoded = true) int id, @Query(value = "token", encoded = true) String token);

    /**
     * cette methode permet de recuperer les informations de l'agence ainsi que les differents departs de l'agence.
     * @param id
     * identifiant de l'agence
     * @param token
     * et le token pour la connection.
     * @return
     */

    @GET("second/Rest/depart_data.php")
    Call<Depart_data<Depart_item>> getDepart_data(@Query(value = "id", encoded = true) int id, @Query(value = "token", encoded = true) String token);

    /**
     * comme son nom l indique cette methode permet de recuoere juste les info d un depart
     * @param restrict_id
     * id du depart a recuperer les informations
     * @param token
     * jeton de connection
     * @return
     */

    @GET("second/Rest/depart_data.php")
    Call<Depart_data<Depart_item>> getOneDepartInfo(@Query(value = "restrict", encoded = true) int restrict_id, @Query(value = "token", encoded = true) String token);

    @GET("second/Rest/comment_data.php")
    Call<Commentaire_data> getAgenceComment(@Query(value = "id", encoded = true) int id_agence, @Query(value = "token", encoded = true) String token);

    @POST("second/Rest/addComment.php")
    Call<OutputResponse> postComment(@Body Comment_obj commentaire);

    //cette methode est en voie de disparition.
    @POST("second/Rest/notifyTransaction.php")
    Call<Boolean> notifyTransaction(@Body int places_restante);

    @GET(API_GET_TOKEN)
    Call<String> braintree_api_get_token();


    @POST(API_CHECK_OUT)
    Call<String> braintree_submit_payment(@Body PaiementObject payO);

    /**
     * methode permettant de recuperer les departs en fonction d'un filtre.
     * @param dfo
     * @return
     */
    @POST(RQUEST_FILTER)
    Call<Depart_data<FilteredDepartItem>> postFilter(@Body DepartFilterObject dfo);



}