# AOS-RISC-V

## Initial Setup/Installation for FireSim

Please refer to the [FireSim documentation](https://docs.fires.im/en/latest/Initial-Setup/index.html).

## Setting up the FireSim Repo

When you set up a manager instance during
the initial setup process,
check out a specific tag, `1.13.6`.

```
git clone https://github.com/firesim/firesim.git
cd firesim
git checkout 1.36.6 # command to check out a specific tag
./build-setup.sh fast
```

## Replacing the Existing "generator" Directory.
```
git clone https://github.com/yonghaekim/AOS-RISC-V.git
rm -rf ~/firesim/generator
cp -rf ./AOS-RISC-V/generator ~/firesim/
```

## Building a New HW Design for AOS-RISC-V

1. In `~/firesim/deploy/config_build.ini`,
- Under `[afibuild]`, add a bucket name:
```
s3bucketname=firesim-aos
```
- Under `[builds]`, add a build recipe name (comment out other things).
```
firesim-boom-singlecore-no-nic-l2-llc4mb-ddr3-aos
```
- Undr `[agfistoshare]`, add a agfi name (comment out other things).
```
firesim-boom-singlecore-no-nic-l2-llc4mb-ddr3-aos
```

2. In `~/firesim/deploy/config_build_recipes.ini`,
- Add a build recipe.
```
[firesim-boom-singlecore-no-nic-l2-llc4mb-ddr3-aos]
DESIGN=FireSim
TARGET_CONFIG=DDR3FRFCFSLLC4MB_FireSimLargeBoomConfig
PLATFORM_CONFIG=F75MHz_BaseF1Config
instancetype=f1.2xlarge
deploytriplet=None
```

3. Build a new HW design (This will take a long time, ~10 hours).
```
firesim buildafi
```

After completed, you will get a new agfi number for your new HW design. \
Refer to the [documentation](https://docs.fires.im/en/latest/Building-a-FireSim-AFI.html) for more details.

## Running FireSim Simulations with AOS-RISC-V

1. In `~/firesim/deploy/config_runtime.ini`,
- Change the default HW config.
```
defaulthwconfig=firesim-boom-singlecore-no-nic-l2-llc4mb-ddr3-aos
```

2. In `~/firesim/deploy/config_hwdb.ini`,
- Add a new HW database.
```
[firesim-boom-singlecore-no-nic-l2-llc4mb-ddr3-aos]
agfi=agfi-062b20613c52a2313 # replace with your agfi after HW build completes
deploytripletoverride=None
customruntimeconfig=None
```

3. Launch FPGA instance(s) following the [documentation](https://docs.fires.im/en/latest/Running-Simulations-Tutorial/index.html).

4. Run binaries!
