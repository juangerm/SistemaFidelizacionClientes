package py.gestion.sifi.modelo;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

@Entity @Getter @Setter
@Table(name="usopuntodetalle")
@Tab(properties="id, usoPuntoCabecera.id, bolsaPunto.id")
@View(members="bolsaPunto")
public class UsoPuntoDetalle {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Hidden @ReadOnly
  private Integer id;

  @ReferenceView("simple")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="idusopuntocabecera", columnDefinition="integer",
              foreignKey=@ForeignKey(name="fk_usopuntodetalle_idusopuntocabecera"))
  @NoCreate @NoModify @NoFrame
  private UsoPuntoCabecera usoPuntoCabecera;

  @ReferenceView("simple")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="idbolsapunto", columnDefinition="integer",
              foreignKey=@ForeignKey(name="fk_usopuntodetalle_idbolsapunto"))
  @NoCreate @NoModify @NoFrame
  private BolsaPunto bolsaPunto;
}
