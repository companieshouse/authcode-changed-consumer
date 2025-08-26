package uk.gov.companieshouse.authcode.changed.common;

import static uk.gov.companieshouse.GenerateEtagUtil.generateEtag;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import uk.gov.companieshouse.api.accounts.associations.model.Association;
import uk.gov.companieshouse.api.accounts.associations.model.Association.ApprovalRouteEnum;
import uk.gov.companieshouse.api.accounts.associations.model.Association.StatusEnum;
import uk.gov.companieshouse.api.accounts.user.model.User;
import uk.gov.companieshouse.api.company.CompanyDetails;

public class TestDataManager {

    private static TestDataManager instance = null;

    private final Map<String, Supplier<Association>> associationDaoSuppliers = new HashMap<>();
    private final Map<String, Supplier<User>> userDtoSuppliers = new HashMap<>();
    private final Map<String, Supplier<CompanyDetails>> companyDetailsDtoSuppliers = new HashMap<>();

    public static TestDataManager getInstance() {
        if (Objects.isNull(instance)) {
            instance = new TestDataManager();
        }
        return instance;
    }

    private void instantiateAssociationDaoSuppliers(){
        final Supplier<Association> MiAssociation001 = () -> new Association()
                .id( "MiAssociation001" )
                .companyNumber( "MICOMP001" )
                .userId( "MiUser001" )
                .status( StatusEnum.CONFIRMED )
                .approvalRoute( ApprovalRouteEnum.AUTH_CODE )
                .etag( generateEtag() );
        associationDaoSuppliers.put( "MiAssociation001", MiAssociation001 );

        final Supplier<Association> MiAssociation002 = () -> new Association()
                .id( "MiAssociation002" )
                .companyNumber( "MICOMP001" )
                .userId( "MiUser002" )
                .status( StatusEnum.CONFIRMED )
                .approvalRoute( ApprovalRouteEnum.AUTH_CODE )
                .etag( generateEtag() );
        associationDaoSuppliers.put( "MiAssociation002", MiAssociation002 );

        final Supplier<Association> MiAssociation012 = () -> new Association()
                .id( "MiAssociation012" )
                .companyNumber( "MICOMP001" )
                .userId( "MiUser003" )
                .status( StatusEnum.CONFIRMED )
                .approvalRoute( ApprovalRouteEnum.AUTH_CODE )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation012", MiAssociation012 );

        final Supplier<Association> MiAssociation013 = () -> new Association()
                .id( "MiAssociation013" )
                .companyNumber( "MICOMP001" )
                .userId( "MiUser004" )
                .status( StatusEnum.CONFIRMED )
                .approvalRoute( ApprovalRouteEnum.INVITATION )
                .approvalExpiryAt(String.valueOf( LocalDateTime.parse(  "1992-05-08T10:30:00.000000" ) ) )
                .approvedAt( OffsetDateTime.from( LocalDateTime.parse(  "1992-05-06T10:30:00.000000" ) ) )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation013", MiAssociation013 );

        final Supplier<Association> MiAssociation014 = () -> new Association()
                .id( "MiAssociation014" )
                .companyNumber( "MICOMP001" )
                .userId( "MiUser005" )
                .status( StatusEnum.AWAITING_APPROVAL )
                .approvalRoute( ApprovalRouteEnum.INVITATION )
                .approvalExpiryAt( String.valueOf( LocalDateTime.parse(  "2020-05-08T10:30:00.000000" ) ) )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation014", MiAssociation014 );

        final Supplier<Association> MiAssociation015 = () -> new Association()
                .id( "MiAssociation015" )
                .companyNumber( "MICOMP001" )
                .userEmail( "largo.lagrande.monkey.island@inugami-example.com" )
                .status( StatusEnum.AWAITING_APPROVAL )
                .approvalRoute( ApprovalRouteEnum.INVITATION )
                .approvalExpiryAt( String.valueOf( LocalDateTime.parse( "2021-05-08T10:30:00.000000" ) ) )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation015", MiAssociation015 );

        final Supplier<Association> MiAssociation016 = () -> new Association()
                .id( "MiAssociation016" )
                .companyNumber( "MICOMP001" )
                .userId( "MiUser007" )
                .status( StatusEnum.AWAITING_APPROVAL )
                .approvalRoute( ApprovalRouteEnum.INVITATION )
                .approvalExpiryAt( String.valueOf( LocalDateTime.parse( "2500-05-08T10:30:00.000000" ) ) )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation016", MiAssociation016 );

        final Supplier<Association> MiAssociation017 = () -> new Association()
                .id( "MiAssociation017" )
                .companyNumber( "MICOMP001" )
                .userEmail( "meathook.monkey.island@inugami-example.com" )
                .status( StatusEnum.AWAITING_APPROVAL )
                .approvalRoute( ApprovalRouteEnum.INVITATION )
                .approvalExpiryAt( String.valueOf( LocalDateTime.parse( "2600-05-08T10:30:00.000000" ) ) )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation017", MiAssociation017 );

        final Supplier<Association> MiAssociation018 = () -> new Association()
                .id( "MiAssociation018" )
                .companyNumber( "MICOMP001" )
                .userId( "MiUser009" )
                .status( StatusEnum.REMOVED )
                .approvalRoute( ApprovalRouteEnum.AUTH_CODE )
                .removedAt( OffsetDateTime.from( LocalDateTime.parse(  "2000-05-01T10:30:00.000000" ) ) )
                .etag( generateEtag() );
        associationDaoSuppliers.put( "MiAssociation018", MiAssociation018 );

        final Supplier<Association> MiAssociation019 = () -> new Association()
                .id( "MiAssociation019" )
                .companyNumber( "MICOMP001" )
                .userEmail( "haggis.mcmutton.monkey.island@inugami-example.com" )
                .status( StatusEnum.REMOVED )
                .approvalRoute( ApprovalRouteEnum.AUTH_CODE )
                .removedAt( OffsetDateTime.from( LocalDateTime.parse(  "2001-05-01T10:30:00.000000" ) ) )
                .etag( generateEtag() );
        associationDaoSuppliers.put( "MiAssociation019", MiAssociation019 );

        final Supplier<Association> MiAssociation020 = () -> new Association()
                .id( "MiAssociation020" )
                .companyNumber( "MICOMP001" )
                .userId( "MiUser011" )
                .status( StatusEnum.REMOVED )
                .approvalRoute( ApprovalRouteEnum.INVITATION )
                .approvalExpiryAt( String.valueOf( LocalDateTime.parse( "1992-05-08T10:30:00.000000" ) ) )
                .removedAt( OffsetDateTime.from( LocalDateTime.parse(  "1992-05-04T10:30:00.000000" ) ) )
                .etag( generateEtag() );
        associationDaoSuppliers.put( "MiAssociation020", MiAssociation020 );

        final Supplier<Association> MiAssociation021 = () -> new Association()
                .id( "MiAssociation021" )
                .companyNumber( "MICOMP001" )
                .userEmail( "morgan.leflay.monkey.island@inugami-example.com" )
                .status( StatusEnum.REMOVED )
                .approvalRoute( ApprovalRouteEnum.INVITATION )
                .approvalExpiryAt( String.valueOf( LocalDateTime.parse( "1992-05-08T10:30:00.000000" ) ) )
                .removedAt( OffsetDateTime.from( LocalDateTime.parse( "1992-05-05T10:30:00.000000" ) ) )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation021", MiAssociation021 );

        final Supplier<Association> MiAssociation022 = () -> new Association()
                .id( "MiAssociation022" )
                .companyNumber( "MICOMP001" )
                .userId( "MiUser013" )
                .status( StatusEnum.REMOVED )
                .approvalRoute( ApprovalRouteEnum.AUTH_CODE )
                .removedAt( OffsetDateTime.from( LocalDateTime.parse( "2000-05-01T10:30:00.000000" ) ) )
                .etag( generateEtag() );
        associationDaoSuppliers.put( "MiAssociation022", MiAssociation022 );

        final Supplier<Association> MiAssociation023 = () -> new Association()
                .id( "MiAssociation023" )
                .companyNumber( "MICOMP001" )
                .userEmail( "three.headed.monkey.monkey.island@inugami-example.com" )
                .status( StatusEnum.REMOVED )
                .approvalRoute( ApprovalRouteEnum.AUTH_CODE )
                .removedAt( OffsetDateTime.from( LocalDateTime.parse( "2000-05-01T10:30:00.000000" ) ) )
                .etag( generateEtag() );
        associationDaoSuppliers.put( "MiAssociation023", MiAssociation023 );

        final Supplier<Association> MiAssociation024 = () -> new Association()
                .id( "MiAssociation024" )
                .companyNumber( "MICOMP001" )
                .userEmail( "apple.bob.monkey.island@inugami-example.com" )
                .status( StatusEnum.AWAITING_APPROVAL )
                .approvalRoute( ApprovalRouteEnum.INVITATION )
                .approvalExpiryAt( String.valueOf( LocalDateTime.parse( "1992-05-08T10:30:00.000000" ) ) )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation024", MiAssociation024 );

        final Supplier<Association> MiAssociation025 = () -> new Association()
                .id( "MiAssociation025" )
                .companyNumber( "MICOMP001" )
                .userId( "MiUser016" )
                .status( StatusEnum.REMOVED )
                .approvalRoute( ApprovalRouteEnum.AUTH_CODE )
                .removedAt( OffsetDateTime.from( LocalDateTime.parse( "2000-05-01T10:30:00.000000" ) ) )
                .etag( generateEtag() );
        associationDaoSuppliers.put( "MiAssociation025", MiAssociation025 );

        final Supplier<Association> MiAssociation026 = () -> new Association()
                .id( "MiAssociation026" )
                .companyNumber( "MICOMP001" )
                .userEmail( "lemonhead.monkey.island@inugami-example.com" )
                .status( StatusEnum.AWAITING_APPROVAL )
                .approvalRoute( ApprovalRouteEnum.INVITATION )
                .approvalExpiryAt( String.valueOf( LocalDateTime.parse( "2992-05-08T10:30:00.000000" ) ) )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation026", MiAssociation026 );

        final Supplier<Association> MiAssociation030 = () -> new Association()
                .id( "MiAssociation030" )
                .companyNumber( "MICOMP001" )
                .userId( "MiUser019" )
                .status( StatusEnum.CONFIRMED )
                .approvalRoute( ApprovalRouteEnum.INVITATION )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation030", MiAssociation030 );

        final Supplier<Association> MiAssociation031 = () -> new Association()
                .id( "MiAssociation031" )
                .companyNumber( "MICOMP001" )
                .userEmail( "navigator.head.monkey.island@inugami-example.com" )
                .status( StatusEnum.AWAITING_APPROVAL )
                .approvalRoute( ApprovalRouteEnum.INVITATION )
                .approvalExpiryAt( String.valueOf( LocalDateTime.parse( "2992-05-08T10:30:00.000000" ) ) )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation031", MiAssociation031 );

        final Supplier<Association> MiAssociation032 = () -> new Association()
                .id( "MiAssociation032" )
                .companyNumber( "MICOMP001" )
                .userEmail( "carla.monkey.island@inugami-example.com" )
                .status( StatusEnum.MIGRATED )
                .approvalRoute( ApprovalRouteEnum.MIGRATION )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation032", MiAssociation032 );

        final Supplier<Association> MiAssociation034 = () -> new Association()
                .id( "MiAssociation034" )
                .companyNumber( "MICOMP001" )
                .userEmail( "mad.marty.monkey.island@inugami-example.com" )
                .status( StatusEnum.MIGRATED )
                .approvalRoute( ApprovalRouteEnum.MIGRATION )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation034", MiAssociation034 );

        final Supplier<Association> MiAssociation035 = () -> new Association()
                .id( "MiAssociation035" )
                .companyNumber( "MICOMP001" )
                .userEmail( "pegnose.pete.monkey.island@inugami-example.com" )
                .status( StatusEnum.MIGRATED )
                .approvalRoute( ApprovalRouteEnum.MIGRATION )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation035", MiAssociation035 );

        final Supplier<Association> MiAssociation036 = () -> new Association()
                .id( "MiAssociation036" )
                .companyNumber( "MICOMP001" )
                .userEmail( "captain.smirk.monkey.island@inugami-example.com" )
                .status( StatusEnum.MIGRATED )
                .approvalRoute( ApprovalRouteEnum.MIGRATION )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation036", MiAssociation036 );

        final Supplier<Association> MiAssociation037 = () -> new Association()
                .id( "MiAssociation037" )
                .companyNumber( "MICOMP001" )
                .userEmail( "cutthroat.bill.monkey.island@inugami-example.com" )
                .status( StatusEnum.MIGRATED )
                .approvalRoute( ApprovalRouteEnum.MIGRATION )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation037", MiAssociation037 );

        final Supplier<Association> MiAssociation038 = () -> new Association()
                .id( "MiAssociation038" )
                .companyNumber( "MICOMP001" )
                .userEmail( "rapp.scallion.monkey.island@inugami-example.com" )
                .status( StatusEnum.MIGRATED )
                .approvalRoute( ApprovalRouteEnum.MIGRATION )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation038", MiAssociation038 );

        final Supplier<Association> MiAssociation039 = () -> new Association()
                .id( "MiAssociation039" )
                .companyNumber( "MICOMP001" )
                .userEmail( "edward.van.helgen.monkey.island@inugami-example.com" )
                .status( StatusEnum.MIGRATED )
                .approvalRoute( ApprovalRouteEnum.MIGRATION )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation039", MiAssociation039 );

        final Supplier<Association> MiAssociation040 = () -> new Association()
                .id( "MiAssociation040" )
                .companyNumber( "MICOMP001" )
                .userEmail( "griswold.goodsoup.monkey.island@inugami-example.com" )
                .status( StatusEnum.MIGRATED )
                .approvalRoute( ApprovalRouteEnum.MIGRATION )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation040", MiAssociation040 );

        final Supplier<Association> MiAssociation041 = () -> new Association()
                .id( "MiAssociation041" )
                .companyNumber( "MICOMP001" )
                .userId( "MiUser026" )
                .status( StatusEnum.REMOVED )
                .approvalRoute( ApprovalRouteEnum.MIGRATION )
                .approvalExpiryAt( String.valueOf( LocalDateTime.parse( "2025-03-10T10:30:00.000000" ) ) )
                .approvedAt( OffsetDateTime.from( LocalDateTime.parse( "2025-03-04T10:30:00.000000" ) ) )
                .removedAt( OffsetDateTime.from( LocalDateTime.parse( "2025-03-05T10:30:00.000000" ) ) )
                .approvalExpiryAt( String.valueOf( LocalDateTime.parse( "2025-03-10T10:30:00.000000" ) ) )
                .approvedAt( OffsetDateTime.from( LocalDateTime.parse( "2025-03-04T10:30:00.000000" ) ) )
                .removedAt( OffsetDateTime.from( LocalDateTime.parse(  "2025-03-05T10:30:00.000000" ) ) )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation041", MiAssociation041 );

        final Supplier<Association> MiAssociation042 = () -> new Association()
                .id( "MiAssociation042" )
                .companyNumber( "MICOMP001" )
                .userId( "MiUser028" )
                .status( StatusEnum.CONFIRMED )
                .approvalRoute( ApprovalRouteEnum.INVITATION )
                .approvalExpiryAt( String.valueOf( LocalDateTime.parse( "1992-05-08T10:30:00.000000" ) ) )
                .approvedAt( OffsetDateTime.from( LocalDateTime.parse( "1992-05-06T10:30:00.000000" ) ) )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation042", MiAssociation042 );

        final Supplier<Association> MiAssociation043 = () -> new Association()
                .id( "MiAssociation043" )
                .companyNumber( "MICOMP001" )
                .userId( "MiUser029" )
                .status( StatusEnum.AWAITING_APPROVAL )
                .approvalRoute( ApprovalRouteEnum.INVITATION )
                .approvalExpiryAt( String.valueOf( LocalDateTime.parse( "2500-05-08T10:30:00.000000" ) ) )
                .etag( generateEtag() );

        associationDaoSuppliers.put( "MiAssociation043", MiAssociation043 );
    }


    private void instantiateUserDtoSuppliers(){
        userDtoSuppliers.put( "MiUser001", () -> new User().userId( "MiUser001" ).email( "guybrush.threepwood.monkey.island@inugami-example.com" ).displayName( "Guybrush Threepwood" ) );
        userDtoSuppliers.put( "MiUser002", () -> new User().userId( "MiUser002" ).email( "lechuck.monkey.island@inugami-example.com" ).displayName( "LeChuck" ) );
        userDtoSuppliers.put( "MiUser003", () -> new User().userId( "MiUser003" ).email( "elaine.marley.monkey.island@inugami-example.com" ).displayName( "Elaine Marley" ) );
        userDtoSuppliers.put( "MiUser004", () -> new User().userId( "MiUser004" ).email( "stan.monkey.island@inugami-example.com" ).displayName( "Stan" ) );
        userDtoSuppliers.put( "MiUser005", () -> new User().userId( "MiUser005" ).email( "wally.monkey.island@inugami-example.com" ).displayName( "Wally" ) );
        userDtoSuppliers.put( "MiUser006", () -> new User().userId( "MiUser006" ).email( "largo.lagrande.monkey.island@inugami-example.com" ).displayName( "Largo LaGrande" ) );
        userDtoSuppliers.put( "MiUser007", () -> new User().userId( "MiUser007" ).email( "murray.monkey.island@inugami-example.com" ).displayName( "Murray" ) );
        userDtoSuppliers.put( "MiUser008", () -> new User().userId( "MiUser008" ).email( "meathook.monkey.island@inugami-example.com" ).displayName( "Meathook" ) );
        userDtoSuppliers.put( "MiUser009", () -> new User().userId( "MiUser009" ).email( "herman.toothrot.monkey.island@inugami-example.com" ).displayName( "Herman Toothrot" ) );
        userDtoSuppliers.put( "MiUser010", () -> new User().userId( "MiUser010" ).email( "haggis.mcmutton.monkey.island@inugami-example.com" ).displayName( "Haggis McMutton" ) );
        userDtoSuppliers.put( "MiUser011", () -> new User().userId( "MiUser011" ).email( "otis.monkey.island@inugami-example.com" ).displayName( "Otis" ) );
        userDtoSuppliers.put( "MiUser012", () -> new User().userId( "MiUser012" ).email( "morgan.leflay.monkey.island@inugami-example.com" ).displayName( "Morgan LeFlay" ) );
        userDtoSuppliers.put( "MiUser013", () -> new User().userId( "MiUser013" ).email( "voodoo.lady.monkey.island@inugami-example.com" ).displayName( "Voodoo Lady" ) );
        userDtoSuppliers.put( "MiUser014", () -> new User().userId( "MiUser014" ).email( "captain.dread.monkey.island@inugami-example.com" ).displayName( "Captain Dread" ) );
        userDtoSuppliers.put( "MiUser015", () -> new User().userId( "MiUser015" ).email( "three.headed.monkey.monkey.island@inugami-example.com" ).displayName( "Three Headed Monkey" ) );
        userDtoSuppliers.put( "MiUser016", () -> new User().userId( "MiUser016" ).email( "kate.capsize.monkey.island@inugami-example.com" ).displayName( "Kate Capsize" ) );
        userDtoSuppliers.put( "MiUser017", () -> new User().userId( "MiUser017" ).email( "mancomb.seepgood.monkey.island@inugami-example.com" ).displayName( "Mancomb Seepgood" ) );
        userDtoSuppliers.put( "MiUser018", () -> new User().userId( "MiUser018" ).email( "pineapplehead.monkey.island@inugami-example.com" ).displayName( "Pineapplehead" ) );
        userDtoSuppliers.put( "MiUser019", () -> new User().userId( "MiUser019" ).email( "bananahead.monkey.island@inugami-example.com" ).displayName( "Bananahead" ) );
        userDtoSuppliers.put( "MiUser020", () -> new User().userId( "MiUser020" ).email( "carla.monkey.island@inugami-example.com" ).displayName( "Sword Master" ) );
        userDtoSuppliers.put( "MiUser021", () -> new User().userId( "MiUser021" ).email( "mad.marty.monkey.island@inugami-example.com" ).displayName( "Mad Marty" ) );
        userDtoSuppliers.put( "MiUser022", () -> new User().userId( "MiUser022" ).email( "pegnose.pete.monkey.island@inugami-example.com" ).displayName( "Pegnose Pete" ) );
        userDtoSuppliers.put( "MiUser023", () -> new User().userId( "MiUser023" ).email( "captain.smirk.monkey.island@inugami-example.com" ).displayName( "Captain Smirk" ) );
        userDtoSuppliers.put( "MiUser024", () -> new User().userId( "MiUser024" ).email( "cutthroat.bill.monkey.island@inugami-example.com" ).displayName( "Cutthroat Bill" ) );
        userDtoSuppliers.put( "MiUser025", () -> new User().userId( "MiUser025" ).email( "rapp.scallion.monkey.island@inugami-example.com" ).displayName( "Rapp Scallion" ) );
        userDtoSuppliers.put( "MiUser026", () -> new User().userId( "MiUser026" ).email( "blondebeard.monkey.island@inugami-example.com" ).displayName( "Blondebeard" ) );
        userDtoSuppliers.put( "MiUser027", () -> new User().userId( "MiUser027" ).email( "ozzie.mandrill.monkey.island@inugami-example.com" ).displayName( "Ozzie Mandrill" ) );
        userDtoSuppliers.put( "MiUser028", () -> new User().userId( "MiUser028" ).email( "ignatius.cheese.monkey.island@inugami-example.com" ).displayName( "Ignatius Cheese" ) );
        userDtoSuppliers.put( "MiUser029", () -> new User().userId( "MiUser029" ).email( "miss.rivers.monkey.island@inugami-example.com" ).displayName( "Miss Rivers" ) );
    }

    private void instantiateCompanyDtoSuppliers(){
        companyDetailsDtoSuppliers.put( "MICOMP001", () -> new CompanyDetails().companyNumber( "MICOMP001" ).companyName( "Scumm Bar" ).companyStatus( "active" ) );
    }

    private TestDataManager(){
        instantiateAssociationDaoSuppliers();
        instantiateUserDtoSuppliers();
        instantiateCompanyDtoSuppliers();
    }

    public List<Association> fetchAssociation( final String... ids  ){
        return Arrays.stream( ids )
                .map( associationDaoSuppliers::get )
                .map( Supplier::get )
                .collect( Collectors.toList() );
    }

    public List<User> fetchUser( final String... ids  ){
        return Arrays.stream( ids )
                .map( userDtoSuppliers::get )
                .map( Supplier::get )
                .collect( Collectors.toList() );
    }

    public List<CompanyDetails> fetchCompanyDetails( final String... ids  ){
        return Arrays.stream( ids )
                .map( companyDetailsDtoSuppliers::get )
                .map( Supplier::get )
                .collect( Collectors.toList() );
    }

}
