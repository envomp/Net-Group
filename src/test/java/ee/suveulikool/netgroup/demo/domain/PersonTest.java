package ee.suveulikool.netgroup.demo.domain;

import ee.suveulikool.netgroup.demo.repository.GeneratePeople;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonTest {

    private Person child;
    private Person sister;
    private Person half_sister;
    private Person father;
    private Person mother;
    private Person aunt;
    private Person grand_mother;

    @Before
    public void init() {
        GeneratePeople generatePeople = new GeneratePeople().invoke();
        child = generatePeople.getChild();
        sister = generatePeople.getSister();
        half_sister = generatePeople.getHalf_sister();
        father = generatePeople.getFather();
        mother = generatePeople.getMother();
        aunt = generatePeople.getAunt();
        grand_mother = generatePeople.getGrand_mother();
        generatePeople.addRelations();
    }

    @Test
    public void isSibling() {
        assert !half_sister.isSibling(child);
        assert !child.isSibling(half_sister);

        assert sister.isSibling(child);
        assert child.isSibling(sister);
    }

    @Test
    public void isHalfSibling() {
        assert half_sister.isHalfSibling(child);
        assert child.isHalfSibling(half_sister);

        assert !sister.isHalfSibling(child);
        assert !child.isHalfSibling(sister);
    }

    @Test
    public void isMother() {
        assert mother.isMother(child);
        assert !grand_mother.isMother(child);
        assert !father.isMother(child);
    }

    @Test
    public void isFather() {
        assert !mother.isFather(child);
        assert !grand_mother.isFather(child);
        assert father.isFather(child);
    }

    @Test
    public void isGrandMother() {
        assert !mother.isGrandMother(child);
        assert grand_mother.isGrandMother(child);
        assert !father.isGrandMother(child);
    }

    @Test
    public void isGrandFather() {
        assert !mother.isGrandFather(child);
        assert !grand_mother.isGrandFather(child);
        assert !father.isGrandFather(child);
    }

    @Test
    public void isAncestor() {
        assert mother.isAncestor(child);
        assert grand_mother.isAncestor(child);
        assert father.isAncestor(child);
    }

    @Test
    public void isBloodRelated() {
        assert mother.isBloodRelated(child) && child.isBloodRelated(mother);
        assert grand_mother.isBloodRelated(child) && child.isBloodRelated(grand_mother);
        assert sister.isBloodRelated(child) && child.isBloodRelated(sister);
        assert !father.isBloodRelated(mother) && !mother.isBloodRelated(father); // That would be weird
    }

    @Test
    public void isDistantlyBloodRelated() {
        assert mother.isDistantlyBloodRelated(child) && child.isDistantlyBloodRelated(mother);
        assert grand_mother.isDistantlyBloodRelated(child) && child.isDistantlyBloodRelated(grand_mother);
        assert sister.isDistantlyBloodRelated(child) && child.isDistantlyBloodRelated(sister);
        assert !father.isDistantlyBloodRelated(mother) && !mother.isDistantlyBloodRelated(father); // That would be weird
    }
}