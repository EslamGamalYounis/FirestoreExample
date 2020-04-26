package com.example.firestoreexampleproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";

    private EditText editTextTitle;
    private EditText editTextDescription;

    private TextView textViewData;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Notebook");
    private DocumentReference noteReference = db.collection("Notebook").document("My First Note");
    private ListenerRegistration noteListener;
    //or db.document("Notebook/My First Note");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription =findViewById(R.id.edit_text_description);
        textViewData =findViewById(R.id.text_view_data);
    }

    @Override
    protected void onStart() {
        super.onStart();

        notebookRef.addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e!= null){
                    return;
                }

                String data ="";
                for(QueryDocumentSnapshot documentSnapshots: queryDocumentSnapshots){
                    Note note = documentSnapshots.toObject(Note.class);

                    note.setDocumentId(documentSnapshots.getId());
                    String documentId =note.getDocumentId();

                    String title = note.getTitle();
                    String description = note.getDescription();
                    data +="ID: "+documentId+ "\nTitle: "+ title +"\n Description: "+ description +"\n\n";
                }

                textViewData.setText(data);
            }
        });


        /*noteListener = noteReference.addSnapshotListener(this,new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e !=null){
                    Toast.makeText(MainActivity.this,"Error while loading",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "on Event: " +e.toString());
                    return;
                }
                if (documentSnapshot.exists()){
                    //String title = documentSnapshot.getString(KEY_TITLE);
                    //String description = documentSnapshot.getString(KEY_DESCRIPTION);
                    //textViewData.setText("title: "+title+"\n" +"description: "+ description);

                    Note note = documentSnapshot.toObject(Note.class);
                    String title = note.getTitle();
                    String description = note.getDescription();
                    textViewData.setText("title: "+title+"\n" +"description: "+ description);
                }
                else {
                    textViewData.setText("");
                }
            }
        });*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        //noteListener.remove();
    }

    public void addNote(View view) {

        String title = editTextTitle.getText().toString();
        String description =editTextDescription.getText().toString();

        /*Map<String ,Object> note = new HashMap<>();
        note.put(KEY_TITLE,title);
        note.put(KEY_DESCRIPTION,description);*/
        //db.collection("NoteBook").document("my first note").set(note);
        Note note =new Note(title,description);

        //add more notes
        notebookRef.add(note).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(MainActivity.this,"Note Saved",Toast.LENGTH_SHORT).show();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Error !",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " +e.toString());
            }
        });

        //update same  note
       /* noteReference.set(note)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(MainActivity.this,"Note Saved",Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,"Error 1",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: " +e.toString());
                }
            });*/
    }

    public void loadNotes(View view) {

        //load notes
        notebookRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                String data ="";
                for(QueryDocumentSnapshot documentSnapshots: queryDocumentSnapshots){
                    Note note = documentSnapshots.toObject(Note.class);
                    note.setDocumentId(documentSnapshots.getId());

                    String documentId =note.getDocumentId();
                    String title = note.getTitle();
                    String description = note.getDescription();
                    data +="ID: "+documentId+ "\nTitle: "+ title +"\n Description: "+ description +"\n\n";
                }

                textViewData.setText(data);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });



        //load one notes
       /* noteReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            //String title = documentSnapshot.getString(KEY_TITLE);
                            //String description = documentSnapshot.getString(KEY_DESCRIPTION);
                            //Map<String,Object> note = documentSnapshot.getData();

                            Note note = documentSnapshot.toObject(Note.class);
                            String title = note.getTitle();
                            String description = note.getDescription();
                            textViewData.setText("title: "+title+"\n" +"description: "+ description);
                        }
                        else{
                            Toast.makeText(MainActivity.this,"Document does not exist",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"Error !",Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " +e.toString());

                    }
                });*/

    }

    /*public void updateDescription(View view) {
        String description = editTextDescription.getText().toString();

        Map<String,Object> note = new HashMap<>();
        note.put(KEY_DESCRIPTION,description);
        //noteReference.set(note, SetOptions.merge());

        //noteReference.update(note);
        noteReference.update(KEY_DESCRIPTION,description);
    }*/

   /*public void deleteDescription(View view) {
        //Map<String,Object> note = new HashMap<>();
        //note.put(KEY_DESCRIPTION, FieldValue.delete());
        //noteReference.update(note);

       noteReference.update(KEY_DESCRIPTION,FieldValue.delete());
    }

    public void deleteNote(View view) {
        noteReference.delete();
    }*/


}
