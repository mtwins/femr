package femr.ui.controllers;

import com.google.inject.Inject;
import femr.business.services.core.IEncounterService;
import femr.business.services.core.IPatientService;
import femr.business.services.core.ISearchService;
import femr.business.services.core.ISessionService;
import femr.common.dtos.CurrentUser;
import femr.common.dtos.ServiceResponse;
import femr.common.models.PatientEncounterItem;
import femr.common.models.PatientItem;
import femr.common.models.TabItem;
import femr.data.models.mysql.PatientEncounter;
import femr.data.models.mysql.Roles;
import femr.ui.helpers.security.AllowedRoles;
import femr.ui.helpers.security.FEMRAuthenticated;
import femr.ui.models.sessions.CreateViewModel;
import femr.ui.models.manager.EditViewModelGet;
import femr.ui.models.history.IndexEncounterMedicalViewModel;
import femr.ui.views.html.manager.index;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;

/**
 * Created by fq251 on 10/20/2016.
 */

@Security.Authenticated(FEMRAuthenticated.class)
@AllowedRoles({Roles.MANAGER})
public class ManagerController extends Controller {

    private final ISessionService sessionService;
    private final IPatientService patientService;
    private final IEncounterService encounterService;
    private final ISearchService searchService;
    private final Form<CreateViewModel> createViewModelForm = Form.form(CreateViewModel.class);

    @Inject
    public ManagerController(ISessionService sessionService, IPatientService patientService,
                             IEncounterService encounterService, ISearchService searchService) {
        this.sessionService = sessionService;
        this.patientService = patientService;
        this.encounterService=encounterService;
        this.searchService=searchService;
    }

    public Result indexGet() {

        CurrentUser currentUser = sessionService.retrieveCurrentUserSession();
        List<PatientItem> p=new ArrayList<PatientItem>();
        List<PatientEncounterItem> encounter=new ArrayList<PatientEncounterItem>();
        EditViewModelGet viewModel = new EditViewModelGet();
        IndexEncounterMedicalViewModel hpimodel= new IndexEncounterMedicalViewModel();



        if (currentUser.getTripId()==null) {
            return ok(index.render(currentUser, viewModel,hpimodel,false));
        }
        ServiceResponse<List<PatientEncounterItem>> patientEncounter=encounterService.returnCurrentDayPatientEncounters(currentUser.getTripId());
        //converts patient encounter Items to patient Items
        for(int i=0;i<patientEncounter.getResponseObject().size();i++) {
            ServiceResponse<PatientItem> translate = searchService.retrievePatientItemByPatientId(patientEncounter.getResponseObject().get(i).getPatientId());
           PatientItem e=translate.getResponseObject();
       ///     TabItem m= hpimodel.getHpiFieldsWithMultipleChiefComplaints().get().get("narrative");
            p.add(e);
        }
      //  viewModel.setFilter();
        //sets Patients Items in view model use
        viewModel.setTriagePatients(p);
       viewModel.setPatientEncounter(patientEncounter.getResponseObject());

            return ok(index.render(currentUser, viewModel, hpimodel, false));

    }

    public Result editPost()
    {

        return redirect(routes.ManagerController.indexGet());

    }


}


