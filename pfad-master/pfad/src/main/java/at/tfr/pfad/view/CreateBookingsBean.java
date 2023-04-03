package at.tfr.pfad.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.event.TabEvent;

import at.tfr.pfad.ConfigurationType;
import at.tfr.pfad.model.Activity;
import at.tfr.pfad.model.Configuration;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Squad;
import at.tfr.pfad.util.QueryExecutor;
import at.tfr.pfad.util.SessionBean;

@Named
@WindowScoped
public class CreateBookingsBean implements Serializable {

	@Inject
	private transient BookingActionBean bookingActionBean;
	@Inject
	private SessionBean sessionBean;
	@Inject
	private QueryExecutor queryExecutor;

	private Activity activity;
	private List<Squad> squads = new ArrayList<>();
	private boolean withAssistants;
	private Activity sourceActivity;
	private Activity targetActivity;

	private enum TabVisible {
		squadBooking, allBooking, sourceBooking, fromTo
	};

	private boolean showFinished;
	private boolean squadBookingVisible;
	private boolean allBookingVisible;
	private boolean sourceBookingVisible;
	private boolean fromToVisible;
	private String squadBookingActiveIndex = "";
	private String allBookingActiveIndex = "";
	private String sourceBookingActiveIndex = "";
	private String fromToActiveIndex = "";
	private List<Configuration> dataSources = Collections.emptyList();
	private List<String> dataSourceKeys = Collections.emptyList();
	private Configuration dataSource;
	private String dataSourceKey;

	@PostConstruct
	public void init() {
		dataSources = sessionBean.getConfig().stream()
				.filter(c -> ConfigurationType.datasource.equals(c.getType())
						|| ConfigurationType.nativeSource.equals(c.getType()))
				.filter(c -> c.getCkey().startsWith("Booking:")).sorted((x, y) -> x.getCkey().compareTo(y.getCkey()))
				.collect(Collectors.toList());
		dataSourceKeys = dataSources.stream().map(c -> c.getCkey().substring("Booking:".length()))
				.collect(Collectors.toList());
	}

	public boolean isShowFinished() {
		return showFinished;
	}

	public void setShowFinished(boolean showFinished) {
		this.showFinished = showFinished;
	}

	public boolean isSquadBookingVisible() {
		return squadBookingVisible;
	}

	public void setSquadBookingVisible(boolean squadBookingVisible) {
		this.squadBookingVisible = squadBookingVisible;
	}

	public boolean isAllBookingVisible() {
		return allBookingVisible;
	}

	public void setAllBookingVisible(boolean allBookingVisible) {
		this.allBookingVisible = allBookingVisible;
	}
	
	public String getSquadBookingActiveIndex() {
		return squadBookingActiveIndex;
	}

	public void setSquadBookingActiveIndex(String squadBookingActiveIndex) {
		this.squadBookingActiveIndex = squadBookingActiveIndex;
	}

	public String getAllBookingActiveIndex() {
		return allBookingActiveIndex;
	}

	public void setAllBookingActiveIndex(String allBookingActiveIndex) {
		this.allBookingActiveIndex = allBookingActiveIndex;
	}

	public String getSourceBookingActiveIndex() {
		return sourceBookingActiveIndex;
	}

	public void setSourceBookingActiveIndex(String sourceBookingActiveIndex) {
		this.sourceBookingActiveIndex = sourceBookingActiveIndex;
	}

	public String getFromToActiveIndex() {
		return fromToActiveIndex;
	}

	public void setFromToActiveIndex(String fromToActiveIndex) {
		this.fromToActiveIndex = fromToActiveIndex;
	}

	public List<Squad> getSquads() {
		return squads;
	}

	public void setSquads(List<Squad> squads) {
		this.squads = squads;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public boolean isWithAssistants() {
		return withAssistants;
	}

	public void setWithAssistants(boolean withAssistants) {
		this.withAssistants = withAssistants;
	}

	public String createBookings() {
		return bookingActionBean.createBookings(squads, activity, withAssistants);
	}

	public void createFromSource() {
		Configuration config = dataSources.get(dataSourceKeys.indexOf(dataSourceKey));
		List<Member> members = (List) queryExecutor.list(config);
		bookingActionBean.createBookingsForMembers(members, targetActivity);
	}

	public String createBookingsForAllActive() {
		return bookingActionBean.createBookingsForAllActive(activity);
	}

	public String createBookingsFromSource() {
		return bookingActionBean.createBookingsFromSource(sourceActivity, targetActivity);
	}

	public Activity getSourceActivity() {
		return sourceActivity;
	}

	public void setSourceActivity(Activity sourceActivity) {
		this.sourceActivity = sourceActivity;
	}

	public Activity getTargetActivity() {
		return targetActivity;
	}

	public void setTargetActivity(Activity targetActivity) {
		this.targetActivity = targetActivity;
	}

	public boolean isFromToVisible() {
		return fromToVisible;
	}

	public void setFromToVisible(boolean fromToVisible) {
		this.fromToVisible = fromToVisible;
	}

	public List<Configuration> getDataSources() {
		return dataSources;
	}

	public List<String> getDataSourceKeys() {
		return dataSourceKeys;
	}

	public Configuration getDataSource() {
		return dataSource;
	}

	public void setDataSource(Configuration dataSource) {
		this.dataSource = dataSource;
	}

	public String getDataSourceKey() {
		return dataSourceKey;
	}

	public void setDataSourceKey(String dataSourceKey) {
		this.dataSourceKey = dataSourceKey;
	}

	public boolean isSourceBookingVisible() {
		return sourceBookingVisible;
	}

	public void setSourceBookingVisible(boolean sourceBookingVisible) {
		this.sourceBookingVisible = sourceBookingVisible;
	}

	public void onTabChange(TabEvent evt) {
		if (evt == null || evt.getTab() == null) 
			return;
		for (TabVisible tv : TabVisible.values()) {
			if (evt.getTab().getId().contains(tv.name())) {
				switch (tv) {
				case squadBooking:
					squadBookingVisible = !squadBookingVisible;
					break;
				case allBooking:
					allBookingVisible = !allBookingVisible;
					break;
				case sourceBooking:
					sourceBookingVisible = !sourceBookingVisible;
					break;
				case fromTo:
					fromToVisible = !fromToVisible;
					break;
				}
			}
		}
	}
}
