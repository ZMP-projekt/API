package service;

import dto.LocationDTO;
import lombok.RequiredArgsConstructor;
import model.GymLocation;
import repository.LocationRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public List<LocationDTO> getAllLocations() {
        return locationRepository.findAll().stream()
            .map(loc -> new LocationDTO(loc.getId(), loc.getName(), loc.getCity(), loc.getAddress()))
            .collect(Collectors.toList());
    }

    public GymLocation getLocationById(Long id) {
        return locationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Nie znaleziono lokalizacji o ID: " + id));
    }
    
}