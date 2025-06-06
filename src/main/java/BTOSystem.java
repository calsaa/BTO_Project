import storage.IStorage;
import storage.Storage;
import system.Login;
import ui.*;
import users.*;
import java.util.List;
import java.util.Objects;



public class BTOSystem {
    List<String> loginUserData; //Basic info of the Current User
    IUser currUser; //Object of the Current User
    private final IUi ui = new Ui();
    private final IStorage storage = new Storage();

    /**
     * ENTRY point of the whole Application
     */
    public static void main(String[] args){
        System.out.println(Messages.BTO_ART+Messages.APPLICATION_NAME);
        new BTOSystem().run();
    }

    public void run(){
        //Initialise
        loginIn();
        exit();
    }

    /**
     * User login, create the type of User and Run their MENU
     */
    private void loginIn(){
        while(!Objects.equals(ui.switchOff(), "0")) {
            Login login = new Login();
            while (loginUserData == null) {
                loginUserData = login.authenticate(ui.readUserID(), ui.readPassword(), storage);
                if (loginUserData == null) {
                    System.out.println("Invalid credentials!");
                    break; //Escape to Starting
                }
            }

            //If Successfully Login
            if(loginUserData!=null) {
                currUser = switch (loginUserData.get(5)) {
                    case "Manager" -> new HDBManager(loginUserData);
                    case "Officer" -> new HDBOfficer(loginUserData);
                    default -> new Applicant(loginUserData);
                };

                //MENU
                if (currUser instanceof HDBManager) {
                    managerMenu();
                } else {
                    if (currUser instanceof HDBOfficer) {
                        //loading info for officer
                        ((HDBOfficer) currUser).checkProjectsAllocated(storage);
                    }
                    normalMenu();
                }
            }

            loginUserData = null;//log out
        }
    }

    /**
     * Menu Used by Applicant and Officer
     */
    public void normalMenu(){
        int choice;
        System.out.println("Hello "+currUser.getName()+", "+currUser.getUserType());
        if (currUser instanceof HDBOfficer) {System.out.println(Messages.OFFICER_MENU);}
        else  {System.out.println(Messages.APPLICANT_MENU);}
        do {
            System.out.print(Messages.printLine()+"Menu ");
            choice = ui.inputInt();
            switch (choice) {
                case 1:
                    currUser.addFilter();
                    break;
                case 2:
                    currUser.viewFilters();
                    break;
                case 3:
                    currUser.removeFilter();
                    break;
                case 4:
                    System.out.print("Enter New Password: ");
                    currUser.changePassword(ui.inputString());
                    storage.updateUserData(currUser.getAllUserData());
                    break;
                case 5:
                    ((Applicant)currUser).viewBTOProject(storage);
                    break;
                case 6:
                    ((Applicant)currUser).applyBTOProject(storage);
                    break;
                case 7:
                    ((Applicant)currUser).viewEnquiries(storage);
                    break;
                case 8:
                    ((Applicant)currUser).addEnquiry(storage);
                    break;
                case 9:
                    ((Applicant)currUser).editEnquiry(storage);
                    break;
                case 10:
                    ((Applicant)currUser).removeEnquiry(storage);
                    break;
                case 11:
                    ((Applicant)currUser).viewBTOApplication(storage);
                    break;
                case 12:
                    ((Applicant)currUser).withdrawBTOApplication(storage);
                case 13:
                    if(currUser instanceof HDBOfficer) {
                        ((HDBOfficer)currUser).registerToJoinProject(storage);
                        break;
                    }
                case 14:
                    if(currUser instanceof HDBOfficer) {
                        System.out.println("Your registration status is: "+((HDBOfficer) currUser).checkRegistrationStatus(storage));
                        break;
                    }
                case 15:
                    if(currUser instanceof HDBOfficer) {
                        ((HDBOfficer)currUser).replyToEnquiry(storage);
                        break;
                    }
                case 16:
                    if(currUser instanceof HDBOfficer) {
                        ((HDBOfficer)currUser).changeBTOApplicationStatus(storage);
                        break;
                    }
            }
        }while(choice != 0);
    }

    /**
     * MENU used by Manager ONLY
     */
    public void managerMenu(){
        int choice;
        System.out.println("Hello "+currUser.getName()+", "+currUser.getUserType());
        System.out.println(Messages.MANAGER_MENU);
        do {
            System.out.print(Messages.printLine()+"Menu ");
            choice = ui.inputInt();
            switch (choice) {
                case 1:
                    currUser.addFilter();
                    break;
                case 2:
                    currUser.viewFilters();
                    break;
                case 3:
                    currUser.removeFilter();
                    break;
                case 4:
                    System.out.print("Enter New Password: ");
                    currUser.changePassword(ui.inputString());
                    storage.updateUserData(currUser.getAllUserData());
                    break;
                case 5:
                	((HDBManager)currUser).createProject(storage);
                    break;
                case 6:
                	((HDBManager)currUser).editProject(storage);
                	break;
                case 7:
                	((HDBManager)currUser).deleteProject(storage);
                    break;
                case 8:
                	((HDBManager)currUser).viewAllProjects(storage);
                    break;
                case 9:
                	((HDBManager)currUser).viewCreatedProject(storage);
                    break;
                case 10:
                	((HDBManager)currUser).toggleProjectVisibility(storage);
                	break;
                case 11:
                	((HDBManager)currUser).viewHDBOfficerRegistration(storage);
                	break;
                case 12:
                	((HDBManager)currUser).decideOfficerApplication(storage);
                	break;
                case 13:
                	((HDBManager)currUser).approveApplicantApplication(storage);
                	break;
                case 14:
                	((HDBManager)currUser).rejectApplicantApplication(storage);
                	break;
                case 15:
                	((HDBManager)currUser).approveWithdrawalRequest(storage);
                	break;
                case 16:
                	((HDBManager)currUser).rejectWithdrawalRequest(storage);  
                	break;
                case 17:
                	((HDBManager)currUser).generateApplicantReport(storage);
                	break;
                case 18:
                	((HDBManager)currUser).viewEnquiries(storage);
                	break;
                case 19:
                	((HDBManager)currUser).replyToEnquiry(storage);      
                	break;
            }
        }while(choice != 0);

    }

    /**
     * Executed when user Closes Application
     */
    private void exit(){
        System.out.println("\nThank you for using our application!");
        storage.close(); //Override All csv files
        System.exit(0);
    }
}