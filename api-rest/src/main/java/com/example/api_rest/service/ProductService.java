@Service
public class ProductService {

    private final ExternalProductClient externalClient;

    public ProductService(ExternalProductClient externalClient) {
        this.externalClient = externalClient;
    }

    public List<ProductDetail> getSimilarProducts(String productId) {
        List<ProductDetail> result = new ArrayList<>();

        try {
            String[] similarIds = externalClient.getSimilarIds(productId);

            if (similarIds != null) {
                List<CompletableFuture<Optional<ProductDetail>>> futures = new ArrayList<>();

                for (String id : similarIds) {
                    futures.add(externalClient.getProductDetail(id));
                }

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                for (CompletableFuture<Optional<ProductDetail>> future : futures) {
                    future.join().ifPresent(result::add);
                }
            }

            return result;

        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Product not found");
        }
    }
}
