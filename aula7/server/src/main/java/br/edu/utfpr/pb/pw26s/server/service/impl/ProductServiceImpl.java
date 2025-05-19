package br.edu.utfpr.pb.pw26s.server.service.impl;

import br.edu.utfpr.pb.pw26s.server.model.Product;
import br.edu.utfpr.pb.pw26s.server.repository.ProductRepository;
import br.edu.utfpr.pb.pw26s.server.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
@Slf4j
public class ProductServiceImpl extends CrudServiceImpl<Product, Long>
    implements ProductService {
    private static final String FILE_PATH = File.separator + "uploads";
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    protected JpaRepository<Product, Long> getRepository() {
        return this.productRepository;
    }

    @Override
    public Product saveImage(MultipartFile file, Product product) {
        File dir = new File(FILE_PATH + File.separator + "images-product");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String suffix = FilenameUtils.getExtension(file.getOriginalFilename());
        productRepository.save(product);
        try {
            FileOutputStream fileOut = new FileOutputStream(
                    new File(dir + File.separator + product.getId() + suffix)
            );
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOut);
            bufferedOutputStream.write(file.getBytes());
            bufferedOutputStream.close();
            fileOut.close();

            product.setImageName(product.getId() + suffix);
            productRepository.save(product);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return  product;
    }

    @Override
    public Product saveImageFile(MultipartFile file, Product product) {
        try {
            String suffix = FilenameUtils.getExtension(file.getOriginalFilename());

            productRepository.save(product);
            product.setImageFileName(product.getId() + suffix);
            product.setImageFile(file.getBytes());

            productRepository.save(product);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return  product;
    }

    @Override
    public String getProductImage(Long id) {
        try {
            Product product = productRepository.findById(id).orElse(null);
            if (product != null) {
                String fileName = FILE_PATH + File.separator + "images-product" +
                        File.separator + product.getImageFileName();

                return enconteFileToBase64(fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    private String enconteFileToBase64(String fileName) throws IOException {
        File file = new File(fileName);
        FileInputStream stream = new FileInputStream(file);
        byte[] encoded = Base64.encodeBase64((IOUtils.toByteArray(stream)));
        stream.close();
        return new String(encoded, StandardCharsets.US_ASCII);
    }
}
