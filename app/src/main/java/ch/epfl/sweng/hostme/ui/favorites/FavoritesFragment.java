package ch.epfl.sweng.hostme.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ch.epfl.sweng.hostme.R;
import ch.epfl.sweng.hostme.database.Auth;
import ch.epfl.sweng.hostme.database.Database;
import ch.epfl.sweng.hostme.ui.search.ApartmentAdapter;
import ch.epfl.sweng.hostme.utils.Apartment;

public class FavoritesFragment extends Fragment {

    private View root;
    private ArrayList<Apartment> apartments;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private final CollectionReference reference = Database.getCollection("favorite_apart");
    private final CollectionReference apartReference = Database.getCollection("apartments");
    private ApartmentAdapter recyclerAdapter;
    private static final String FAVORITES = "favorites";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = root.findViewById(R.id.favorites_recyclerView);

        reference.document(Auth.getUid()).addSnapshotListener((value, error) -> {
            if (value != null && value.exists()) {
                setUpRecyclerView();
            }
        });
        apartments = new ArrayList<>();
        setUpRecyclerView();

        return root;
    }

    /**
     * Set up the page with all the favorite apartments of the user
     */
    private void setUpRecyclerView() {
        String uid = Auth.getUid();
        reference.document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    apartments.clear();
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        List<String> apartIDs = (List<String>) doc.get(FAVORITES);
                        assert apartIDs != null;
                        if (apartIDs.isEmpty()) {
                            apartments.clear();
                            displayRecycler(apartments);
                        } else {
                            for (String apartID : apartIDs) {
                                apartReference.document(apartID)
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                DocumentSnapshot doc1 = task1.getResult();
                                                Apartment apartment = doc1.toObject(Apartment.class);
                                                apartments.add(apartment);
                                            }
                                            displayRecycler(apartments);
                                        });
                            }
                        }
                    }

                });


    }

    private void displayRecycler(List<Apartment> apartments) {
        List<Apartment> apartmentsWithoutDuplicate = new ArrayList<>(new HashSet<>(apartments));
        recyclerAdapter = new ApartmentAdapter(apartmentsWithoutDuplicate);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}