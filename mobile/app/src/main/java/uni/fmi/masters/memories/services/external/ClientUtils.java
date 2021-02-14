package uni.fmi.masters.memories.services.external;

public class ClientUtils {

    private ClientUtils(){};

    public static final String API_URL = "http://10.0.2.2:8000/api/";

    public static CategoryService getCategoryService() {
        return ExternalClient.getExternalClient(API_URL).create(CategoryService.class);
    }

    public static MemoryService getMemoryService() {
        return ExternalClient.getExternalClient(API_URL).create(MemoryService.class);
    }

}
