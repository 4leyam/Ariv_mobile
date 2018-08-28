package cg.code.aleyam.nzela_nzela.centrale;

public interface CentraleInterface {

    /**
     * methode permettant de mettre a jour les frament principaux de l'activite centrale
     * sur son etat.
     */
    void centraleOnPause();

    /**
     * comme son nom l'indique il joue le meme role que la methode ci dessus
     */
    void centraleOnResume();

    /**
     * permet de partager des objets aux fragment principaux de l'activite par le biais des cles.
     * @param key
     * @param forShare
     */
    void centraleNotification(String key , Object forShare);

    /**
     * permet de traiter les evenment survenu dans les sous fragment (fragment des fragments de centrale_activite)
     * @param key permet d'identifier l'objet passer en paramettre
     * @param value valeure a traiter.
     */
    void fragmentEventHandler(String key , Object value);

}
