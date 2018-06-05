package com.suppergerrie2.headpets.entities.ai;

import com.suppergerrie2.headpets.entities.EntityHeadPet;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISit extends EntityAIBase
{
    private final EntityHeadPet pet;
    /** If the EntityTameable is sitting. */
    private boolean isSitting;

    public EntityAISit(EntityHeadPet entityIn)
    {
        this.pet = entityIn;
        this.setMutexBits(5);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.pet.isInWater())
        {
            return false;
        }
        else if (!this.pet.onGround)
        {
            return false;
        }
        else
        {
            EntityLivingBase entitylivingbase = this.pet.getOwner();

            if (entitylivingbase == null)
            {
                return true;
            }
            else
            {
                return this.pet.getDistanceSq(entitylivingbase) < 144.0D && entitylivingbase.getRevengeTarget() != null ? false : this.isSitting;
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.pet.getNavigator().clearPath();
        isSitting = true;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
    	isSitting = false;
    }

    public boolean isSitting()
    {
    	return this.isSitting;
    }

	public void setSitting(boolean b) {
		this.isSitting = b;		
	}
}