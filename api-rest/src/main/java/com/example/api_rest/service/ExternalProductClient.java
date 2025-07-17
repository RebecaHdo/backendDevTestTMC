@Service
public class ExternalProductClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String EXTERNAL_API_BASE = "http://localhost:3001";

    public String[] getSimilarIds(String productId) {
        return restTemplate.getForObject(
                EXTERNAL_API_BASE + "/product/" + productId + "/similarids",
                String[].class
        );
    }

    @Async("taskExecutor")
    public CompletableFuture<Optional<ProductDetail>> getProductDetail(String id) {
        try {
            ProductDetail detail = restTemplate.getForObject(
                    EXTERNAL_API_BASE + "/product/" + id,
                    ProductDetail.class
            );
            return CompletableFuture.completedFuture(Optional.ofNullable(detail));
        } catch (HttpClientErrorException.NotFound e) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
    }
}
