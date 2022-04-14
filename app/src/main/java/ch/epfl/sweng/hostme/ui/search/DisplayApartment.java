package ch.epfl.sweng.hostme.ui.search;

import static ch.epfl.sweng.hostme.utils.Constants.ADDR;
import static ch.epfl.sweng.hostme.utils.Constants.AREA;
import static ch.epfl.sweng.hostme.utils.Constants.CITY;
import static ch.epfl.sweng.hostme.utils.Constants.KEY_COLLECTION_USERS;
import static ch.epfl.sweng.hostme.utils.Constants.KEY_EMAIL;
import static ch.epfl.sweng.hostme.utils.Constants.KEY_FIRSTNAME;
import static ch.epfl.sweng.hostme.utils.Constants.KEY_LASTNAME;
import static ch.epfl.sweng.hostme.utils.Constants.LEASE;
import static ch.epfl.sweng.hostme.utils.Constants.NPA;
import static ch.epfl.sweng.hostme.utils.Constants.PROPRIETOR;
import static ch.epfl.sweng.hostme.utils.Constants.RENT;
import static ch.epfl.sweng.hostme.utils.Constants.UID;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import ch.epfl.sweng.hostme.R;
import ch.epfl.sweng.hostme.database.Database;
import ch.epfl.sweng.hostme.ui.IOnBackPressed;
import ch.epfl.sweng.hostme.ui.messages.ChatActivity;
import ch.epfl.sweng.hostme.users.User;
import ch.epfl.sweng.hostme.utils.Constants;

public class DisplayApartment extends Fragment implements IOnBackPressed, OnMapReadyCallback  {

    public static final String FROM = "from";
    private final CollectionReference reference = Database.getCollection(KEY_COLLECTION_USERS);
    private View root;
    private BottomNavigationView bottomNav;
    private String fullAddress;

    public DisplayApartment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.display_apartment, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            bottomNav = getActivity().findViewById(R.id.nav_view);
            bottomNav.setVisibility(View.GONE);
            String addr = bundle.getString(ADDR);
            int area = bundle.getInt(AREA, 0);
            int rent = bundle.getInt(RENT, 0);
            String lease = bundle.getString(LEASE);
            String proprietor = bundle.getString(PROPRIETOR);
            String city = bundle.getString(CITY);
            int npa = bundle.getInt(NPA, 0);
            fullAddress = addr + " " + city + " " + npa;
            ImageView image = root.findViewById(R.id.apart_image);
            Bitmap bitmap = bundle.getParcelable(ApartmentAdapter.BITMAP);
            image.setImageBitmap(bitmap);

            changeText(String.valueOf(npa), R.id.npa);
            changeText(city, R.id.city);
            changeText(addr, R.id.addr);
            changeText(String.valueOf(area), R.id.area);
            changeText(String.valueOf(rent), R.id.price);
            changeText(lease, R.id.lease);
            changeText(proprietor, R.id.proprietor);

            String uid = bundle.getString(UID);
            Button contactUser = root.findViewById(R.id.contact_user_button);
            contactUser.setOnClickListener(view -> {
                chatWithUser(uid);
            });
        }

        SupportMapFragment mapFragment = (SupportMapFragment)  getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return root;
    }


    /**
     * launch the activity to chat with the owner of the apartment
     *
     * @param uid
     */
    private void chatWithUser(String uid) {
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                for (DocumentSnapshot doc : snapshot.getDocuments()) {
                    if (doc.getId().equals(uid)) {
                        User user = new User(doc.getString(KEY_FIRSTNAME) + " " +
                                doc.getString(KEY_LASTNAME),
                                null, doc.getString(KEY_EMAIL), null, null);
                        Intent newIntent = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
                        newIntent.putExtra(Constants.KEY_USER, user);
                        newIntent.putExtra(FROM, "apartment");
                        startActivity(newIntent);
                        getActivity().finish();
                    }
                }
            }
        });
    }

    /**
     * change the text view to display the data
     *
     * @param addr
     * @param id
     */
    private void changeText(String addr, int id) {
        TextView addrText = root.findViewById(id);
        addrText.setText(addr);
    }

    @Override
    public boolean onBackPressed() {
        bottomNav.setVisibility(View.VISIBLE);
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng epfl = new LatLng(46.522117, 6.566144);
        googleMap.addMarker(new MarkerOptions()
                .position(epfl)
                .title("EPFL"));
        Geocoder coder = new Geocoder(this.getContext());
        List<Address> address;
        try {
            address = coder.getFromLocationName(this.fullAddress,1);
            Address location = address.get(0);
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition pos = new CameraPosition.Builder().target(latlng)
                            .zoom(12.5f)
                            .build();
            googleMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .title("Your futur home!"));
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
