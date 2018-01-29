package com.malikbouras.heycafe;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malikbouras.heycafe.helpers.PrefManager;
import com.malikbouras.heycafe.model.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    /**
     * Choose an arbitrary request code value
     */
    private static final int RC_SIGN_IN = 123;
    /**
     * TAG
     */
    private static final String TAG = MainActivity.class.getCanonicalName();
    private RecyclerView recyclerView;
    private CoffeeAdapter mAdapter;
    private DatabaseReference usersRef;
    private DatabaseReference coffeesRef;
    private int coffees = 0;
    private List<User> userList = new ArrayList<>();
    private DatabaseReference database;
    private ChildEventListener userListener;
    private String uid;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in

            recyclerView = findViewById(R.id.list);
            progressBar = findViewById(R.id.progressBar2);
            mAdapter = new CoffeeAdapter(userList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);


            database = FirebaseDatabase.getInstance().getReference();
            uid = auth.getCurrentUser().getUid();
            writeNewUser(auth.getCurrentUser().getUid(), auth.getCurrentUser().getDisplayName(), coffees);

            usersRef = database.child("users").getRef();

            coffeesRef = database.child("users").child(uid).getRef();
            coffeesRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        coffees = dataSnapshot.getValue(Integer.class);
                    } catch (DatabaseException e) {
                        Log.e("Coffe number", "ceci n'est pas du café : " + e.getMessage());
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    coffees = dataSnapshot.getValue(Integer.class);

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    coffees = dataSnapshot.getValue(Integer.class);

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    coffees = dataSnapshot.getValue(Integer.class);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            initListner();


            //


        } else {
            // not signed in
            startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI.getInstance().createSignInIntentBuilder().build(), RC_SIGN_IN);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);


            // Successfully signed in
            if (resultCode == RESULT_OK) {
                //startActivity(new Intent(this, CafeActivity.class)
                //       .putExtra("user_token", response.getIdpToken()));
                //finish();
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.no_internet_connection);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar(R.string.unknown_error);
                    return;
                }
            }

            showSnackbar(R.string.unknown_sign_in_response);
        }
    }

    private void showSnackbar(int message) {
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.main_view),
                getResources().getString(message), Snackbar.LENGTH_SHORT);
        mySnackbar.show();

    }

    public void addCoffee(View view) {
        database.child("users").child(uid).child("coffees").setValue(coffees + 1);
        //database.child("users").child(uid).child("coffees").setValue(7);
        showSnackbar(R.string.coffee_taken);

    }

    private void writeNewUser(String userId, String name, int coffees) {
        final User user = new User(name, coffees);
        final DatabaseReference specificUserRef = database.child("users").child(userId);


        specificUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    specificUserRef.setValue(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initListner() {
        // Read from the database
        final ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // A new data item has been added, add it to the list
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    userList.add(user);
                }
                progressBar.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A data item has changed
                User user = dataSnapshot.getValue(User.class);
                User userToDelete = new User();
                int i = 0;
                int userIndex = 0;
                for (User u : userList) {
                    if (u.getName().equals(user.getName())) {
                        //userToDelete = u;
                        userIndex = i;
                    }
                    i++;
                }
                userList.remove(userIndex);
                userList.add(userIndex, user);

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A data item has been removed
                User message = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A data item has changed position
                //Comment movedComment = dataSnapshot.getValue(Comment.class);
                User message = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
                Toast.makeText(MainActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        };

        usersRef.addChildEventListener(childEventListener);

        // copy for removing at onStop()
        userListener = childEventListener;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //FIXME: attention au listner ref user!
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (userListener != null) {
            usersRef.removeEventListener(userListener);
        }

        for (User user : userList) {
            Log.e(TAG, "listItem: " + user.getName());
        }
    }
}
