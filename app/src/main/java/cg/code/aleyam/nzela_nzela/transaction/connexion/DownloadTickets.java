package cg.code.aleyam.nzela_nzela.transaction.connexion;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import cg.code.aleyam.nzela_nzela.actu.connexion.Upload;

public class DownloadTickets {
    private static DownloadTickets instance = null;
    private TransactionCallback transactionCallback = null;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public static DownloadTickets getInstance() {
        if(instance == null) {
            instance = new DownloadTickets();
        }
        return instance;
    }

    private DownloadTickets() {

    }

    public void downloadMyTickects(final DownloadCallback dc) {

        if(auth == null || auth.getCurrentUser() == null) {
            dc.failed("vous n'avez pas de compte N-N" , false);
        } else {
            String user_Pnumber = auth.getCurrentUser().getPhoneNumber();
            DatabaseReference root = Upload.data_root_ref;
            Query ticketQuery = root.child(PostTransaction.transaction_root).orderByKey().equalTo(user_Pnumber);
            final ArrayList<TransactionPost> myTransactions = new ArrayList<>();
            ticketQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            for(DataSnapshot ds : snapshot.getChildren()) {
                                TransactionGet tp = ds.getValue(TransactionGet.class);
                                tp.setDate(Long.parseLong(ds.getKey()));
                                myTransactions.add(tp);
                                Log.e("test" , tp.getCode());
                            }

                        }
                        Collections.reverse(myTransactions);
                        dc.downloaded(myTransactions);
                    } else {
                        //pas d enfant.
                        dc.failed("Aucun Ticket pour l'instant veuillez selectionner une destination," +
                                " reservez ou achetez votre place puis vous verrez apparaitre vos tickets par ici :) " , false);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    dc.failed("Impossible de charger vos Nzela-tickets pour l'instant; Verifiez votre connection Internet puis reessayez" , false);
                }
            });
            ticketQuery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                for(DataSnapshot ds : dataSnapshot.getChildren()) {
//
//                    dc.succes(ds.getValue(TransactionGet.class));
//
//                }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }


}