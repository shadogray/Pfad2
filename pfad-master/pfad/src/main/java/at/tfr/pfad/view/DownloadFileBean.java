package at.tfr.pfad.view;

import at.tfr.pfad.dao.MemberRepository;
import at.tfr.pfad.model.Member;
import jakarta.enterprise.inject.Model;
import jakarta.inject.Inject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Model
public class DownloadFileBean extends BaseBaseBean {

    @Inject
    protected transient MemberRepository memberRepo;

    public StreamedContent getMemberDeclarationFile(Long memberId) {
        try {
            Member member = memberRepo.findBy(memberId);
            final String fileName = member.getMemDeclUrl();
            final String fnl = fileName.toLowerCase();
            Path storageDir = getMemberDeclarationsDir(member.getId());
            final InputStream fileContent = Files.newInputStream(storageDir.resolve(fileName));
            StreamedContent file = DefaultStreamedContent.builder()
                    .name(fileName)
                    .contentType(fnl.matches("\\.docx?") ? "application/msword" :
                            fnl.endsWith(".pdf") ? "application/pdf" : "application/octet-stream")
                    .stream(() -> fileContent)
                    .build();
            return file;
        } catch (Exception e) {
            log.info("fileDownload: "+e, e);
            error(e.getMessage());
        }
        return null;
    }
}
