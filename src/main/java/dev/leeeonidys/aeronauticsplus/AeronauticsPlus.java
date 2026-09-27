package dev.leeeonidys.aeronauticsplus;

import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aeronautics Plus — aircraft parts for Create Aeronautics.
 *
 * Э0 findings (javap, 27.09.2026):
 *  - BasePropellerBlock (public abstract, extends DirectionalKineticBlock, IBE<BasePropellerBlockEntity>)
 *  - BasePropellerBlockEntity (public abstract, extends KineticBlockEntity,
 *      implements sable.api.block.propeller.BlockEntityPropeller + BlockEntitySubLevelPropellerActor)
 *      -> subclass contract: getConfigThrust() / getConfigAirflow() / getRadius() + createBehavior()
 *  - PropellerActorBehaviour (public ctor: SmartBlockEntity, BlockEntityPropeller; addSimpleLayer(radius, pitch))
 *  - GyroscopicPropellerBearingBlockEntity: setTilt / setStrictTilt / applyTilt + sable$physicsTick
 *      -> helicopter cyclic/collective control is feasible (Э4).
 *
 * Roadmap: Э1 scaffold (this commit) -> Э2 v0.1 prototype propeller (CI must be green first)
 *          -> Э3 family sizes/materials -> Э4 helicopter rotors -> Э5 wings/control surfaces
 *          -> Э6 jet nozzles (TFMG fuel via shim).
 *
 * Code license: MIT. Assets: own artwork only (CA/VW assets are All Rights Reserved).
 */
@Mod(AeronauticsPlus.MODID)
public final class AeronauticsPlus {
    public static final String MODID = "aeronauticsplus";
    public static final Logger LOGGER = LoggerFactory.getLogger("AeronauticsPlus");

    public AeronauticsPlus() {
        LOGGER.info("Aeronautics Plus 0.1.0 scaffold loaded. Э2 (prototype propeller) lands next.");
    }
}
